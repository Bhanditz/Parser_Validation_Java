package org.bibalex.eol.dwca.parser.models;

import java.util.ArrayList;

/*
This class will include the needed objects and attributes
 */
public class NodeRecord {
    String scientificName;
    String taxonId;
    ArrayList<VernacularName> vernaculars;
    ArrayList<Reference> references;
    ArrayList<Occurrence> occurrences;
    ArrayList<Association> associations;
    ArrayList<MeasurementOrFact> measurementOrFacts;
    ArrayList<Media> media;
    Relation relation;
    ArrayList<Agent> agents;

    public NodeRecord(String scientificName, String taxonId) {
        this.scientificName = scientificName;
        this.taxonId = taxonId;
    }

    public ArrayList<Association> getAssociations() {
        return associations;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setAssociations(ArrayList<Association> associations) {
        this.associations = associations;
    }

    public ArrayList<MeasurementOrFact> getMeasurementOrFacts() {
        return measurementOrFacts;
    }

    public void setMeasurementOrFacts(ArrayList<MeasurementOrFact> measurementOrFacts) {
        this.measurementOrFacts = measurementOrFacts;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
    }

    public String getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(String taxonId) {
        this.taxonId = taxonId;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public ArrayList<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(ArrayList<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }

    public ArrayList<Reference> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<Reference> references) {
        this.references = references;
    }

    public String getScientificName() {

        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public ArrayList<VernacularName> getVernaculars() {
        return vernaculars;
    }

    public void setVernaculars(ArrayList<VernacularName> vernaculars) {
        this.vernaculars = vernaculars;
    }

}
