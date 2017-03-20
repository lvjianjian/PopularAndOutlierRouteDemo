package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by lzj on 2016/8/11.
 */
public class Trajectory extends Route{


    private double score;


    public Trajectory(){
        super(0,1);
    }

    public Trajectory(long id) {
        super(id, 1);
    }

    public Trajectory(long id,List<Long> edge_ids) {
        super(1, edge_ids);
        setId(id);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
