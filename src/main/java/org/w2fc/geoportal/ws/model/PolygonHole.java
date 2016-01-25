package org.w2fc.geoportal.ws.model;

/**
 * @author Yevhen
 */
public class PolygonHole {

    private Double[] lats;
    private Double[] lons;

    public PolygonHole() {
    }

    public Double[] getLats() {
        return lats;
    }

    public void setLats(Double[] lats) {
        this.lats = lats;
    }

    public Double[] getLons() {
        return lons;
    }

    public void setLons(Double[] lons) {
        this.lons = lons;
    }
}
