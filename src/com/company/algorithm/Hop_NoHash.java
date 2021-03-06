package com.company.algorithm;

import com.company.element.DataStructure;
import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.LinkedList;

public class Hop_NoHash extends Algo{
    public Graph graph;
    private Integer INF;
    public Integer iterate;
    public boolean print;
    public Integer mode;
    public boolean checkGraph;
    public int dve;
    public int bve;
    public int dve2;
    public int bve2;
    int shortestD;

    public Hop_NoHash(Graph graph){
        this.graph = graph;
        this.INF = Integer.MAX_VALUE;
        this.mode = 0;
        this.iterate = 0;
        this.print = true;
        this.checkGraph = false;
    }

    public void start(){
        iterate = 0;
        dve = 0;
        bve = 0;
        dve2 = 0;
        bve2 = 0;
        while(bfs()){
            iterate+=1;
            for(Vertex v:graph.freeV(Label.A)){
                //assert v.matching == null;
                dfs(v);
            }
        }
    }

    private boolean bfs(){
        //generate an empty queue
        //LinkedList<Vertex> queue = new LinkedList<>();
        DataStructure.CycleArray<Vertex> queue = new DataStructure.CycleArray<>(graph.vertices.size());
        //DataStructure.CycleArray queue = new DataStructure.CycleArray(graph.vertices.size()/2);
        shortestD = INF;

        //set all free As' distance to 0, and push them to the queue
        //set all un-free As' distance to INF
        //Bs' distances are not stored since B->A is always unique (matching)
        for(Vertex v:graph.V(Label.A)){
            if(v.isFree()){
                queue.addLast(v);
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }


        while (!queue.isEmpty()){
            /*
             * v (- A
             * u (- B
             */
            Vertex v = queue.pop();
            if(v.distance < shortestD){
                this.bve+=1;
                for(Vertex u:v.edges){
                    /*
                     * Because we only apply HK on the current Graph
                     * So we check if u is in the Graph
                     * If it is, it's easy to see its matching must be in the current graph.
                     */
                    bve2+=1;
                    if(!checkGraph || u.piece == v.piece){
                        /*
                         * <dist(u.matching) == INF> means
                         * <u.matching> is free or <u.matching> has not been explored yet (in the current BFS)
                         * In either case, we update the dist of <u.matching>
                         */
                        //dist.get(u.matching) == INF

                        if(u.matching == null){
                            shortestD = v.distance + 1;
                        }
                        else if(u.matching.distance == INF){
                            u.matching.distance = v.distance + 1;
                            queue.addLast(u.matching);
                        }
                    }
                }
            }
        }
        //In <dist.get(null)> != INF, means <null> has been explored, which means a free vertex has been explored
        if(print){
            System.out.println("Hop bfs returns: " + shortestD);
        }

        return shortestD != INF;
    }

    private boolean dfs(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */
        if(v == null){
            return true;
        }
        this.dve+=1;
        for(Vertex u:v.edges){
            dve2+=1;
            if(!checkGraph || u.piece == v.piece){
                int nextDist;
                if(u.matching == null){
                    nextDist = shortestD;
                }
                else{
                    nextDist = u.matching.distance;
                }
                if(nextDist == v.distance + 1){
                    if(dfs(u.matching)){
                        u.match(v);
                        return true;
                    }
                }
            }
        }
        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        v.distance = INF;
        return false;
    }
}
