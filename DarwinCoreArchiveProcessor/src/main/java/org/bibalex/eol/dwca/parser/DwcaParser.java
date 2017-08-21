package org.bibalex.eol.dwca.parser;


import org.apache.commons.io.FilenameUtils;
import org.bibalex.eol.dwca.parser.models.*;
import org.bibalex.eol.dwca.parser.utils.CommonTerms;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveFactory;
import org.gbif.dwca.record.Record;
import org.gbif.dwca.record.StarRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DwcaParser {

    Archive dwca;
    HashMap<String, Reference> referencesMap;
    HashMap<String, Agent> agentsMap;
    HashMap<String, Association> associationMap;
    HashMap<String, MeasurementOrFact> measurementOrFactHashMap;

    public DwcaParser(Archive dwca) {
        this.dwca = dwca;
        referencesMap = new HashMap<String, Reference>();
        agentsMap = new HashMap<String, Agent>();
        associationMap = new HashMap<String, Association>();
        measurementOrFactHashMap = new HashMap<String, MeasurementOrFact>();
        loadAllReferences();
        loadAllAgents();
        loadAllAssociations();
        loadAllMeasurementOrFacts();
    }

    public void prepareNodesRecord() {
        for (StarRecord rec : dwca) {
            NodeRecord tableRecord = new NodeRecord(rec.core().value(DwcTerm.scientificName),
                    rec.core().value(DwcTerm.taxonID));

            tableRecord.setRelation(parseTaxon(rec));

            if (rec.hasExtension(GbifTerm.VernacularName)) {
                tableRecord.setVernaculars(parseVernacularNames(rec));
            }
            if(rec.hasExtension(CommonTerms.occurrenceTerm)){
                tableRecord.setOccurrences(parseOccurrences(rec));
            }
            if(rec.hasExtension(CommonTerms.mediaTerm)){
                tableRecord.setMedia(parseMedia(rec, tableRecord));
            }

            adjustReferences(tableRecord);
            adjustAgents(tableRecord);

            printRecord(tableRecord);
            System.out.println();
        }
    }

    private void adjustReferences(NodeRecord nodeRecord){
        ArrayList<Reference> refs = nodeRecord.getReferences();
        ArrayList<String> refIds = new ArrayList<String>();

        if(refs != null) {
            for (Reference ref : refs)
                refIds.add(ref.getReferenceId());
        }

        if(nodeRecord.getRelation().getReferenceId() != null &&
                !refIds.contains(nodeRecord.getRelation().getReferenceId()))
            addReference(nodeRecord, referencesMap.get(nodeRecord.getRelation().getReferenceId()));

        if(nodeRecord.getMedia() != null) {
            for (Media media : nodeRecord.getMedia()) {
                if (media.getReferenceId() != null && !refIds.contains(media.getReferenceId()))
                    addReference(nodeRecord, referencesMap.get(media.getReferenceId()));
            }
        }

        if(nodeRecord.getAssociations() != null) {
            for (Association association : nodeRecord.getAssociations()) {
                if (association.getReferenceId() != null && !refIds.contains(association.getReferenceId()))
                    addReference(nodeRecord, referencesMap.get(association.getReferenceId()));
            }
        }

        if(nodeRecord.getMeasurementOrFacts() != null) {
            for (MeasurementOrFact measurementOrFact : nodeRecord.getMeasurementOrFacts()) {
                if (measurementOrFact.getReferenceId() != null && !refIds.contains(measurementOrFact.getReferenceId()))
                    addReference(nodeRecord, referencesMap.get(measurementOrFact.getReferenceId()));
            }
        }
    }

    private void addReference(NodeRecord nodeRecord, Reference ref){
        ArrayList<Reference> refs = nodeRecord.getReferences();
        if(nodeRecord.getReferences() != null)
            refs.add(ref);
        else{
            refs = new ArrayList<Reference>();
            refs.add(ref);
            nodeRecord.setReferences(refs);
        }
    }

    private void adjustAgents(NodeRecord nodeRecord){
        if(nodeRecord.getMedia() != null) {
            ArrayList<Agent> agents = nodeRecord.getAgents();
            ArrayList<String> agentsIds = new ArrayList<String>();

            if(agents != null) {
                for (Agent agent : agents)
                    agentsIds.add(agent.getAgentId());
            }

            for (Media media : nodeRecord.getMedia()) {
                if (media.getAgentId() != null && !agentsIds.contains(media.getAgentId()))
                    addAgent(nodeRecord, agentsMap.get(media.getAgentId()));
            }
        }
    }

    private void addAgent(NodeRecord nodeRecord, Agent agent){
        ArrayList<Agent> agents = nodeRecord.getAgents();
        if(nodeRecord.getAgents() != null)
            agents.add(agent);
        else{
            agents = new ArrayList<Agent>();
            agents.add(agent);
            nodeRecord.setAgents(agents);
        }
    }

    private void loadAllReferences(){
        System.out.println(dwca.getExtension(CommonTerms.referenceTerm));
        if(dwca.getExtension(CommonTerms.referenceTerm) != null) {
            for (Iterator<Record> it = dwca.getExtension(CommonTerms.referenceTerm).iterator(); it.hasNext(); ) {
                parseReference(it.next());
            }
        }
    }

    private void loadAllAgents(){
        if(dwca.getExtension(CommonTerms.agentTerm) != null){
            for (Iterator<Record> it = dwca.getExtension(CommonTerms.agentTerm).iterator(); it.hasNext(); ) {
                parseAgent(it.next());
            }
        }
    }

    private void loadAllAssociations(){
        if(dwca.getExtension(CommonTerms.associationTerm) != null){
            for (Iterator<Record> it = dwca.getExtension(CommonTerms.associationTerm).iterator(); it.hasNext(); ) {
                parseAssociation(it.next());
            }
        }
    }

    private void loadAllMeasurementOrFacts(){
        if(dwca.getExtension(DwcTerm.MeasurementOrFact) != null){
            for (Iterator<Record> it = dwca.getExtension(DwcTerm.MeasurementOrFact).iterator(); it.hasNext(); ) {
                parseMeasurementOrFact(it.next());
            }
        }
    }

    private ArrayList<VernacularName> parseVernacularNames(StarRecord record){
        ArrayList<VernacularName> vernaculars = new ArrayList<VernacularName>();
        for (Record extensionRecord : record.extension(GbifTerm.VernacularName)) {
            VernacularName vName = new VernacularName(extensionRecord.value(DwcTerm.vernacularName),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.sourceURI)),
                    extensionRecord.value(CommonTerms.languageTerm),
                    extensionRecord.value(DwcTerm.locality), extensionRecord.value(DwcTerm.countryCode),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.isPreferredNameURI)),
                    extensionRecord.value(DwcTerm.taxonRemarks));
            vernaculars.add(vName);
        }
        return vernaculars;
    }

    private Relation parseTaxon(StarRecord record){
        Relation relation = new Relation(record.core().value(DwcTerm.parentNameUsageID),
                record.core().value(DwcTerm.kingdom), record.core().value(DwcTerm.phylum),
                record.core().value(DwcTerm.class_), record.core().value(DwcTerm.order),
                record.core().value(DwcTerm.family), record.core().value(DwcTerm.genus),
                record.core().value(CommonTerms.referenceIDTerm));
        return relation;
    }

    private ArrayList<Occurrence> parseOccurrences(StarRecord record){
        ArrayList<Occurrence> occurrences = new ArrayList<Occurrence>();
        for (Record extensionRecord : record.extension(CommonTerms.occurrenceTerm)) {
            Occurrence occ = new Occurrence(extensionRecord.value(DwcTerm.occurrenceID),
                    extensionRecord.value(DwcTerm.eventID), extensionRecord.value(DwcTerm.institutionCode),
                    extensionRecord.value(DwcTerm.collectionCode), extensionRecord.value(DwcTerm.catalogNumber),
                    extensionRecord.value(DwcTerm.sex), extensionRecord.value(DwcTerm.lifeStage),
                    extensionRecord.value(DwcTerm.reproductiveCondition), extensionRecord.value(DwcTerm.behavior),
                    extensionRecord.value(DwcTerm.establishmentMeans), extensionRecord.value(DwcTerm.occurrenceRemarks),
                    extensionRecord.value(DwcTerm.individualCount), extensionRecord.value(DwcTerm.preparations),
                    extensionRecord.value(DwcTerm.fieldNotes), extensionRecord.value(DwcTerm.samplingProtocol),
                    extensionRecord.value(DwcTerm.samplingEffort), extensionRecord.value(DwcTerm.recordedBy),
                    extensionRecord.value(DwcTerm.identifiedBy), extensionRecord.value(DwcTerm.dateIdentified),
                    extensionRecord.value(DwcTerm.eventDate), extensionRecord.value(CommonTerms.modifiedDateTerm),
                    extensionRecord.value(DwcTerm.locality), extensionRecord.value(DwcTerm.decimalLatitude),
                    extensionRecord.value(DwcTerm.decimalLongitude), extensionRecord.value(DwcTerm.verbatimLatitude),
                    extensionRecord.value(DwcTerm.verbatimLongitude), extensionRecord.value(DwcTerm.verbatimElevation));
            occurrences.add(occ);
        }
        return occurrences;
    }

    private void parseMeasurementOrFact(Record record){
            MeasurementOrFact measurementOrFact = new MeasurementOrFact(record.value(DwcTerm.measurementID),
                    record.value(DwcTerm.occurrenceID),
                    record.value(TermFactory.instance().findTerm(TermURIs.measurementOfTaxonURI)),
                    record.value(CommonTerms.associationIDTerm),
                    record.value(TermFactory.instance().findTerm(TermURIs.parentMeasurementIDURI)),
                    record.value(DwcTerm.measurementType), record.value(DwcTerm.measurementValue),
                    record.value(DwcTerm.measurementUnit), record.value(DwcTerm.measurementAccuracy),
                    record.value(TermFactory.instance().findTerm(TermURIs.statisticalMethodURI)),
                    record.value(DwcTerm.measurementDeterminedDate), record.value(DwcTerm.measurementDeterminedBy),
                    record.value(DwcTerm.measurementMethod), record.value(DwcTerm.measurementRemarks),
                    record.value(CommonTerms.sourceTerm), record.value(CommonTerms.bibliographicCitationTerm),
                    record.value(CommonTerms.contributorTerm), record.value(CommonTerms.referenceIDTerm));
        measurementOrFactHashMap.put(record.value(DwcTerm.measurementID), measurementOrFact);
    }

    private void parseAssociation(Record record){
        Association association = new Association(record.value(CommonTerms.associationIDTerm),
                record.value(DwcTerm.occurrenceID),
                record.value(TermFactory.instance().findTerm(TermURIs.associationType)),
                record.value(TermFactory.instance().findTerm(TermURIs.targetOccurrenceID)),
                record.value(DwcTerm.measurementDeterminedDate), record.value(DwcTerm.measurementDeterminedBy),
                record.value(DwcTerm.measurementMethod), record.value(DwcTerm.measurementRemarks),
                record.value(CommonTerms.sourceTerm), record.value(CommonTerms.bibliographicCitationTerm),
                record.value(CommonTerms.contributorTerm), record.value(CommonTerms.referenceIDTerm));
        associationMap.put(record.value(CommonTerms.associationIDTerm), association);
    }

    private void parseReference(Record record){
        Reference ref = new Reference(record.value(CommonTerms.identifierTerm),
                record.value(TermFactory.instance().findTerm(TermURIs.publicationTypeURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.fullReferenceURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.primaryTitleURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.titleURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.pagesURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.pageStartURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.pageEndURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.volumeURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.editionURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.publisherURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.authorsListURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.editorsListURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.createdURI)),
                record.value(CommonTerms.languageTerm),
                record.value(TermFactory.instance().findTerm(TermURIs.uriURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.doiURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.localityOfPublisherURI)));
        referencesMap.put(record.value(CommonTerms.identifierTerm), ref);
    }

    private void parseAgent(Record record){
        Agent agent  = new Agent(record.value(CommonTerms.identifierTerm),
                record.value(TermFactory.instance().findTerm(TermURIs.agentNameURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentFirstNameURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentFamilyNameURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentRole)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentEmailURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentHomepageURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentLogoURLURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentProjectURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentOrganizationURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentAccountNameURI)),
                record.value(TermFactory.instance().findTerm(TermURIs.agentOpenIdURI)));
        agentsMap.put(record.value(CommonTerms.identifierTerm), agent);
    }

    private ArrayList<Media> parseMedia(StarRecord record, NodeRecord rec){
        ArrayList<Media> media = new ArrayList<Media>();
        for (Record extensionRecord : record.extension(CommonTerms.mediaTerm)) {
            Media med  = new Media(extensionRecord.value(CommonTerms.identifierTerm),
                    extensionRecord.value(CommonTerms.typeTerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaSubtypeURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaFormatURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaSubjectURI)),
                    extensionRecord.value(CommonTerms.titleTerm),
                    extensionRecord.value(CommonTerms.descriptionTerm),
                    extensionRecord.value(CommonTerms.accessURITerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.thumbnailUrlURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaFurtherInformationURLURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaDerivedFromURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaCreateDateURI)),
                    extensionRecord.value(CommonTerms.modifiedDateTerm),
                    extensionRecord.value(CommonTerms.languageTerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaRatingURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaAudienceURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.usageTermsURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaRightsURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaOwnerURI)),
                    extensionRecord.value(CommonTerms.bibliographicCitationTerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.publisherURI)),
                    extensionRecord.value(CommonTerms.contributorTerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaCreatorURI)),
                    extensionRecord.value(CommonTerms.agentIDTerm),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaLocationCreatedURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaSpatialURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaLatURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaLonURI)),
                    extensionRecord.value(TermFactory.instance().findTerm(TermURIs.mediaPosURI)),
                    extensionRecord.value(CommonTerms.referenceIDTerm));
            media.add(med);
        }
        return media;
    }

    private void printRecord(NodeRecord nodeRecord){

        System.out.print("Media ");
        if(nodeRecord.getMedia() != null && nodeRecord.getMedia().size() > 0)
            System.out.print(nodeRecord.getMedia().get(0).getMediaId() + " " +
                    nodeRecord.getMedia().get(0).getType() + " " + nodeRecord.getMedia().size());

        System.out.print(" Occ ");

        if(nodeRecord.getOccurrences() != null && nodeRecord.getOccurrences().size() > 0)
            System.out.print(nodeRecord.getOccurrences().get(0).getSex() + " " +
                    nodeRecord.getOccurrences().get(0).getBehavior() + " " + nodeRecord.getOccurrences().size());

        System.out.print(" Vernaculars ");

        if(nodeRecord.getVernaculars() != null && nodeRecord.getVernaculars().size() > 0)
            System.out.print(nodeRecord.getVernaculars().get(0).getName() + " " +
                    nodeRecord.getVernaculars().get(0).getSource() + " " + nodeRecord.getVernaculars().size());

        System.out.print(" Measu ");

        if(nodeRecord.getMeasurementOrFacts() != null && nodeRecord.getMeasurementOrFacts().size() > 0)
            System.out.print(nodeRecord.getMeasurementOrFacts().get(0).getMeasurementId() + " " +
                    nodeRecord.getMeasurementOrFacts().get(0).getContributor() + " " + nodeRecord.getMeasurementOrFacts().size());

        System.out.print(" ass ");

        if(nodeRecord.getAssociations() != null && nodeRecord.getAssociations().size() > 0)
            System.out.print(nodeRecord.getAssociations().get(0).getAssociationId() + " " +
                    nodeRecord.getAssociations().get(0).getContributor() + " " + nodeRecord.getAssociations().size());

        System.out.print(" agents ");
        if(nodeRecord.getAgents() != null && nodeRecord.getAgents().size() > 0)
            System.out.print(nodeRecord.getAgents().get(0).getAgentId() + " " + nodeRecord.getAgents().size());

        System.out.print(" refs ");

        if(nodeRecord.getReferences() != null && nodeRecord.getReferences().size() > 0)
            System.out.print(nodeRecord.getReferences().get(0).getDoi() + " " +
                    nodeRecord.getReferences().get(0).getFullReference() + " " + nodeRecord.getReferences().size());
    }

    public static void main(String [] args){
        Archive dwcArchive = null;
        String path = "/home/ba/EOL_Recources/4.tar.gz";
        try {
            File myArchiveFile = new File(path);
            File extractToFolder = new File(FilenameUtils.removeExtension(path) + ".out");
            dwcArchive = ArchiveFactory.openArchive(myArchiveFile, extractToFolder);
        } catch (Exception e) {
            System.out.println("Failure");
        }
        DwcaParser dwcaP = new DwcaParser(dwcArchive);
        dwcaP.prepareNodesRecord();
    }

}
