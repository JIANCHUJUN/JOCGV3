package com.company.algorithm;

import com.company.element.DataStructure;
import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;



public class Jocg_Pointer extends Algo{
    private class CycleRegion{
        public int top,bot,len;
        public Vertex root;
        public Vertex leaf;
        public CycleRegion(){
            top = -1; bot = -1; root = null; leaf = null; len = 0;
        }
        public void init(){
            top = -1; bot = -1; root = null; leaf = null; len = 0;
        }
    }

    private class TempPath{
        public ArrayList<Vertex> path;
        public ArrayList<CycleRegion> cycleTracker;
        public TempPath(){
            path = new ArrayList<>();
            cycleTracker = new ArrayList<>();
        }

        public Vertex get(int index){
            return path.get(index);
        }

        public int size(){
            return path.size();
        }

        public void addC(CycleRegion c){
            assert c.top == path.size() - 1;
            CycleRegion topHost = cycleTracker.get(c.top);
            if(topHost == null || topHost.root.indexInPath > c.root.indexInPath){
                if(addBot(c)){
                    destroyC(topHost);
                    cycleTracker.set(c.top,c);
                }
            }
        }

        private boolean addBot(CycleRegion c){
            if(c.bot > c.top){
                return false;
            }
            CycleRegion botHost = cycleTracker.get(c.bot);

            if(botHost == null || botHost.root.indexInPath >= c.root.indexInPath){
                if(botHost != null){
                    int a = 0;
                }
                destroyC(botHost);
                cycleTracker.set(c.bot,c);
                return true;
            }
            else{
                botHost.bot+=1;
                return addBot(c);
            }
        }

        public void destroyC(CycleRegion c){
            if(c == null){
                return;
            }
            if(cycleTracker.get(c.top) == c){
                cycleTracker.set(c.top,null);
            }
            if(cycleTracker.get(c.bot) == c){
                cycleTracker.set(c.bot,null);
            }

        }

        public void addV(Vertex v){
            if(v != null){
                v.indexInPath = path.size();
            }
            path.add(v);
            cycleTracker.add(null);
        }

        public boolean isEmpty(){
            return path.isEmpty();
        }

//        private void destroyC(CycleRegion c){
//            if(c != null && cycleTracker != null){
//                if(cycleTracker.get(c.top) == c){
//                    cycleTracker.set(c.top,null);
//                }
//                if(cycleTracker.get(c.bot) == c){
//                    cycleTracker.set(c.bot,null);
//                }
//            }
//        }

        public void removeTopV(){
            Vertex remove = path.remove(path.size() - 1);
            CycleRegion cycleRegion = cycleTracker.remove(cycleTracker.size()-1);


            if(cycleRegion != null){

                if(remove == cycleRegion.root){
                    remove.endInPath = null;
                }
                else{
                    if(remove.endInPath == null
                            || remove.endInPath.indexInPath == -1
                            || remove.endInPath.indexInPath >= cycleRegion.root.indexInPath){
                        remove.endInPath = cycleRegion.root;
                    }

                    cycleRegion.top-=1;
                    if(cycleRegion.top > cycleRegion.bot){
                        CycleRegion topHost = cycleTracker.get(cycleRegion.top);
                        if(topHost == null || topHost.root.indexInPath > cycleRegion.root.indexInPath){
                            destroyC(topHost);
                            cycleTracker.set(cycleRegion.top,cycleRegion);
                        }
                        else{
                            destroyC(cycleRegion);
                        }
                    }
                }
            }
            remove.indexInPath = -1;
        }
    }

    private int INF;
    private int currentBFS;
    public int dve;
    public int dve2;
    public int bve;
    public int bve2;
    public double delP;
    private double totalExplored;
    private double totalCompleteDel;
    public long delTime;
    public int iterate;

    public int pre_dve;
    public int pre_dve2;
    public int pre_bve2;
    public int pre_bve;
    public int pre_ite;
    public long preprocess;
    private long start;
    private long end;
    int shortestD;
    public Graph graph;

    ArrayList<Vertex> exloredV;

    ArrayList<Vertex> path;

    private ArrayList<CycleRegion> nCycle;

    private double vInCycle;
    private double vRemain;
    private double cRemain;
    private double cInCyle;



    DataStructure.CycleArray<DataStructure.DisV> zeros;
    DataStructure.CycleArray<DataStructure.DisV> ones;
    DataStructure.CycleArray<DataStructure.DisV> twos;
    DataStructure.CycleArray<DataStructure.DisV> temp;

    public Jocg_Pointer(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;

        zeros = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        ones = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        twos = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
    }

    public void start(){
        dve=0;
        dve2=0;
        bve2=0;
        bve=0;
        pre_bve=0;
        pre_dve=0;
        pre_ite=0;
        delP=0;
        delTime=0;

        vInCycle = 0;
        vRemain = 0;
        cInCyle = 0;
        cRemain = 0;

        start = System.currentTimeMillis();
        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.checkGraph = true;
            hop.start();
            this.pre_dve+=hop.dve;
            this.pre_bve+=hop.bve;
            this.pre_dve2+=hop.dve2;
            this.pre_bve2+=hop.bve2;
            this.pre_ite+=hop.iterate;
        }
        if(!graph.pieces.isEmpty()){
            this.pre_ite/=graph.pieces.size();
        }
        end = System.currentTimeMillis();
        preprocess = end - start;
        iterate = 0;
        //exloredVtable = new int[graph.vertices.size()/2];
        exloredV = new ArrayList<>();
        while(newbfs()){
            iterate+=1;
            /*
             * visitedE: store all visited edges in the current phase
             * any edges in vistedE won't be explored in the current phase
             */

            //visitedE = new AdjMatrix(graph.numV);
            //visitedE = new HashMap<>();
            int count = 0;
            for(Vertex v:graph.freeV(Label.A)){
                /*
                 * tempE: store visited edges in dfs(v) -> an adjacent list
                 * path: store the augmenting path returned by dfs(v)
                 */

                //tempE = new AdjMatrix(graph.numV);
                //tempE = new HashMap<>();
                path = new ArrayList<>();
                nCycle = new ArrayList<>();
                //tempE, path will be updated in newdfs
                count += 1;
                if(newdfs(v)){
                    /*
                     * iterate the path
                     * if an path is in an affected piece, add this piece to affectedP
                     */
                    Vertex parent = null;
                    for(Vertex uu:path){
                        for(Vertex u:new Vertex[]{uu, uu.matching}){
                            if(parent!=null && parent.piece == u.piece){
                                //u.piece.affectedP = true;
                                u.piece.affectedP.bfs = currentBFS;
                                u.piece.affectedP.dfs = count;
                            }
                            parent = u;
                        }
                    }

                    //count number of cycles
                    cInCyle += nCycle.size();
                    for(CycleRegion c:nCycle){
                        if(c.root.indexInPath == -1){
                            vRemain+=c.len;
                            cRemain+=1;
                        }
                        vInCycle+=c.len;
                    }
                }

                start = System.currentTimeMillis();
                totalExplored += exloredV.size();
                if(!exloredV.isEmpty()){

                    for(Vertex ev:exloredV){
                        if(ev.deepRoot() != null || ev.indexInPath != -1){
                            totalCompleteDel+=ev.deleteE_clean_return(currentBFS,count);

                        }
                        else {
                            totalCompleteDel+=ev.deleteE_clean_return(-1,-1);
                        }

//                       totalCompleteDel+= ev.deleteE_clean_return(currentBFS,count);


                        ev.endInPath = null;
                        ev.explored = false;
                    }
                    for(Vertex pv:path){
                        pv.indexInPath = -1;
                    }
                    exloredV = new ArrayList<>();
                }
                end = System.currentTimeMillis();

                delTime+=(end-start);

            }
        }
        delP = totalCompleteDel;
        double ratioV = vRemain/vInCycle;
        double ratioC = cRemain/cInCyle;
        int a= 0;
    }

    private boolean newbfs(){
        shortestD = INF;
        for(Vertex v:graph.V(Label.A)){
            if(v.isFree()){
                zeros.addLast(new DataStructure.DisV(v,0));
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!(zeros.isEmpty()&&ones.isEmpty()&&twos.isEmpty())){
            if(zeros.isEmpty()){
                while (zeros.isEmpty()){
                    temp = zeros;
                    zeros = ones;
                    ones = twos;
                    twos = temp;
                }
            }

            Vertex v;
            DataStructure.DisV dv;
            dv = zeros.pop();

            v = dv.v;



            if(dv.d > shortestD){
                continue;
            }
            if(v == null){
                shortestD = dv.d;
                continue;
            }
            if(v.distance > shortestD){
                continue;
            }
            if(dv.d > v.distance){
                continue;
            }
            v.reset();
            this.bve+=1;
            for(Vertex u:v.edges){
                this.bve2+=1;
                if(u == v.matching){
                    continue;
                }
                u.reset();
                int distance;
                if(u.matching != null){
                    distance =  graph.getWeight(v,u) + graph.getWeight(u,u.matching);
                }
                else{
                    distance = graph.getWeight(v,u);
                }

                if(v.distance + distance > shortestD){
                    continue;
                }
                if(u.matching == null || u.matching.distance > v.distance + distance){
                    if(u.matching != null){
                        u.matching.distance = v.distance + distance;
                    }
                    if(distance == 0){
                        zeros.addLast(new DataStructure.DisV(u.matching,v.distance));
                    }
                    else if(distance == 1){
                        ones.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                    else{
                        twos.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                }
            }
        }
        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs(Vertex startV){
        TempPath tempPath = new TempPath();

        tempPath.addV(startV);
        startV.explored = true;
        exloredV.add(startV);
        while(!tempPath.isEmpty()){

            Vertex v = tempPath.get(tempPath.size()-1);

            if(v == null){
                //augment path
                Vertex head = null;
                for(Vertex tail:tempPath.path){
                    if(head != null && head.label == Label.A){
                        head.match(tail);
                        path.add(head);
                        path.add(tail);
                        head.endInPath = null;
                        tail.endInPath = null;
                    }
                    head = tail;
                }
                return true;
            }

            //assert tempPath.get(v.indexInPath) == v;
            Vertex u = v.next();
            if(u == null){
                tempPath.removeTopV();
                if(!tempPath.isEmpty()){
                    tempPath.removeTopV();
                }
                continue;
            }


            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                CycleRegion cycleRegion;
                if(u.matching != null){
                    if(u.matching.explored){
                        if(u.indexInPath != -1){
                            //assert u.piece == v.piece;
                            cycleRegion = new CycleRegion();
                            cycleRegion.top = v.indexInPath; cycleRegion.bot = u.indexInPath; cycleRegion.root = u; cycleRegion.leaf = v;
                            cycleRegion.len = cycleRegion.top - cycleRegion.bot;
                            tempPath.addC(cycleRegion);
                            nCycle.add(cycleRegion);
                            //isCycleInPiece(tempPath,cycleRegion);

                        }
                        else if(u.endInPath != null
                                && u.endInPath.indexInPath != -1
                                && dist(v) == dist(u.endInPath.matching) - graph.getWeight(u.endInPath,u.endInPath.matching)){
                            cycleRegion = new CycleRegion();
                            cycleRegion.top = v.indexInPath; cycleRegion.bot = u.endInPath.indexInPath; cycleRegion.root = u.endInPath;
                            cycleRegion.leaf = v; cycleRegion.len = cycleRegion.top - cycleRegion.bot;
                            tempPath.addC(cycleRegion);
                            nCycle.add(cycleRegion);
                            //isCycleInPiece(tempPath,cycleRegion);

                        }


                        continue;
                    }
                    if(!u.hasNext()){
                        continue;
                    }
                }
                tempPath.addV(u);

                Vertex next = u.next();
                if(next != null){
                    next.explored = true;
                    exloredV.add(next);
                }
                tempPath.addV(next);
            }
        }
        return false;
    }

    private boolean isCycleInPiece(TempPath tp, CycleRegion c){
        for(int i = c.bot; i < c.top; i++){
            Vertex v = tp.path.get(i);
            Vertex u = tp.path.get(i+1);
            if(u.piece != v.piece){
                System.out.println("Get youuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
                return false;
            }
        }
        return true;
    }


    private boolean newdfs_recur(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }
        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }
        dve+=1;
        while(true){
            Vertex u = v.next();
            if(u == null){
                break;
            }

            dve2+=1;
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }
                if(newdfs(u.next())){
                    u.match(v);
                    path.add(u);
                    path.add(v);
                    //v.reserve(0);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
        return false;
    }

    private boolean newbfs_temp(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }
            v.reset();

            this.bve+=1;
            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);
                        if(graph.getWeight(u,v) == 0){
                            if(u.matching == null){
                                shortestD = v.distance;
                            }
                            else if(graph.getWeight(u,u.matching) == 0
                                    && u.matching.distance > v.distance){
                                u.matching.distance = v.distance;
                                u.reset();
                                cycleArray.addFirst(new DataStructure.DisV(u.matching,u.matching.distance));
                            }
                            else {
                                cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            }
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                        }

                    }

//                        if(graph.getWeight(u,v) == 0){
//                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
//                            //marker[u.id] = 1;
//                        }
//                        else{
//                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
//                            //marker[u.id] = 1;
//                        }
                    }
            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }


    private boolean newbfs_backup(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }

            v.reset();

            this.bve+=1;
            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(v.label == Label.A && u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);

                        if(graph.getWeight(u,v) == 0){
                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            //marker[u.id] = 1;
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                            //marker[u.id] = 1;
                        }
                    }
                }

            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }



    private boolean newdfs_back(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }

        //add v to tempE
        //v (- A
        //parent (- B

        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }

        dve+=1;

        while(true){
            Vertex u = v.next();

            if(u == null){
                break;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }

                if(newdfs(u.next())){
                    u.match(v);
                    path.add(u);
                    path.add(v);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
        return false;
    }

    private int dist(Vertex v){
        if(v == null){
            return shortestD;
        }
        else{
            return v.distance;
        }
    }
}
