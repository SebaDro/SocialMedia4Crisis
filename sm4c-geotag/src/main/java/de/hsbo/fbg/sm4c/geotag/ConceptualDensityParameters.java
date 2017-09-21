/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.geotag;

/**
 *
 * @author Sebastian Drost
 */
public class ConceptualDensityParameters {

    private int relevantSynsets;
    private int totalSynsets;
    private int frequencyRank;

    public ConceptualDensityParameters() {

    }

    public int getRelevantSynsets() {
        return relevantSynsets;
    }

    public void setRelevantSynsets(int relevantSynsets) {
        this.relevantSynsets = relevantSynsets;
    }

    public int getTotalSynsets() {
        return totalSynsets;
    }

    public void setTotalSynsets(int totalSynsets) {
        this.totalSynsets = totalSynsets;
    }

    public int getFrequencyRank() {
        return frequencyRank;
    }

    public void setFrequencyRank(int frequencyRank) {
        this.frequencyRank = frequencyRank;
    }

}
