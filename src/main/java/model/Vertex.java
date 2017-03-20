package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public class Vertex {
    private long id;
    private double latitude;
    private double longitude;
    private double cost;
    private double costForDis;
    private Vertex parent;

    private Set<Vertex> nextVertices;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Vertex(long id,double latitude,double longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nextVertices = new HashSet<Vertex>();
    }


    public double getCostForDis() {
        return costForDis;
    }

    public void setCostForDis(double costForDis) {
        this.costForDis = costForDis;
    }

    public Set<Vertex> getNextVertices() {
        return nextVertices;
    }

    public void setNextVertices(Set<Vertex> nextVertices) {
        this.nextVertices = nextVertices;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }
}
