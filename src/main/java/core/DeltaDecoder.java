package core;

import data_processing.Parameters;
import models.elemental.Job;
import util.General;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;


public class DeltaDecoder {


    ProblemManager problemManager;
    private Parameters parameters;


    public DeltaDecoder(ProblemManager problemManager, Parameters parameters) {
        this.problemManager = problemManager;
        this.parameters = parameters;
    }


    public void decode(Result result) throws IOException {
        //this.KTNSVerified(result);

        this.KTNS(result);

        //this.KTNSHalf(result);
        //this.KTNSVerified(result);
        //this.KTNSGroundTruth(result);

        this.evaluate(result);
    }

    public void evaluate(Result result) {
        //result.setnSwitches(nSwitches(count_switches(result)));
        //result.setnSwitches(nSwitchesSetupBased(result));
        result.setnSwitches(nSwitchesDelta(result));
        result.setCost((double) result.getnSwitches());
    }

    public void KTNS(Result result) throws IOException {
        result.setKtnsId(result.getKtnsId() + 1);
        result.setnReductions(0);

        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {
            Job job = result.getJobAtSeqPos(seqPos);
            if (seqPos != 0) {
                int prevJobId = result.getJobIdAtSeqPos(seqPos - 1);
                int nextJobSeqPos = seqPos + 1;
                int nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);
                int rightJobId = result.getJobIdAtSeqPos(nextJobSeqPos);

                int nToolsAdd = Math.max(0,this.problemManager.getMAGAZINE_SIZE() - job.getSet().length);

                while(nToolsAdd != 0 & nextJobId != -1) {
                    for(int toolId: job.getAntiSet()) {
                        if(nToolsAdd == 0){
                            break;
                        }

                        if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId()) && result.isToolUsedAtJobId(toolId, nextJobId)) {
                            result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                            nToolsAdd--;


                            if(rightJobId != -1) {
                                if(result.isToolUsedAtJobId(toolId,rightJobId)) {
                                    result.setnReductions(result.getnReductions() + 1);
                                }
                            }

                        }
                    }
                    nextJobSeqPos = nextJobSeqPos + 1;
                    nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);
                }

                //Add remaining tools

                for(int toolId: job.getAntiSet()) {
                    if(nToolsAdd == 0) {
                        break;
                    }

                    if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId())) {
                        result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                        nToolsAdd--;


                        if(rightJobId != -1) {
                            if(result.isToolUsedAtJobId(toolId,rightJobId)) {
                                result.setnReductions(result.getnReductions() + 1);
                            }
                        }
                    }
                }

            }
        }

        //this.problemManager.getLogger().writeResult(result);

    }

    public void KTNSHalf(Result result) throws IOException {
        //System.out.println("= = = = ");

        //result.setJobToolMatrix(General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX()));
        result.setKtnsId(result.getKtnsId() + 1);
        result.setnReductions(0);

        //for all jobs
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobAtSeqPos(seqPos);

            //base case
            if (seqPos == 0) {
                //resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);
            }else{

                Job prevJob = result.getJobAtSeqPos(seqPos-1);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(result.getJobToolMatrix()[prevJob.getId()], job.getAntiSet(), result.getKtnsId());

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;
                //System.out.println(nToolsAdd);


                int nextJobPos = seqPos + 1;
                int rightJobId = result.getJobIdAtSeqPos(nextJobPos);
                Job nextJob = result.getJobAtSeqPos(nextJobPos);


                while(nToolsAdd != 0 & nextJob != null & diffPrevCurTools.size() > 0) {

                    ListIterator<Integer> iter = diffPrevCurTools.listIterator();

                    while (iter.hasNext() && nToolsAdd != 0) {
                        int toolAddId = iter.next();

                        if(result.isToolUsedAtJobId(toolAddId, nextJob.getId())) {
                            result.getJobToolMatrix()[job.getId()][toolAddId] = result.getKtnsId();
                            nToolsAdd-=1;
                            iter.remove();

                            //Check if KTNS resolves a possible tool switch next to the current job

                            if(rightJobId != -1) {
                                if(result.isToolUsedAtJobId(toolAddId,rightJobId)) {
                                    result.setnReductions(result.getnReductions() + 1);
                                }
                            }
                        }

                    }
                    nextJobPos = nextJobPos + 1;
                    nextJob = result.getJobAtSeqPos(nextJobPos);
                }


                //Fill remaining nToolsAdd with any tool
                ListIterator<Integer> remainingIter = diffPrevCurTools.listIterator();
                while(nToolsAdd != 0) {
                    int toolAddId = remainingIter.next();
                    result.getJobToolMatrix()[job.getId()][toolAddId] = result.getKtnsId();
                    remainingIter.remove();
                    nToolsAdd-=1;

                    if(rightJobId != -1) {
                        if(result.isToolUsedAtJobId(toolAddId,rightJobId)) {
                            result.setnReductions(result.getnReductions() + 1);
                        }
                    }
                }

            }

        }
        //System.out.println(result.getnReductions());
        //General.printGridP(result);
        //this.problemManager.getLogger().writeResult(result);
    }


    public void KTNSVerified(Result result) throws IOException {

        result.setJobToolMatrix(General.copyGrid(this.problemManager.getJOB_TOOL_MATRIX()));

        //for all jobs
        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobAtSeqPos(seqPos);

            //base case
            if (seqPos == 0) {
                //resultJobToolMatrix[job.getId()] = Arrays.copyOf(job.getTOOLS(),job.getTOOLS().length);
            }else{

                Job prevJob = result.getJobAtSeqPos(seqPos-1);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(result.getJobToolMatrix()[prevJob.getId()], job.getAntiSet());

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;


                int nextJobPos = seqPos + 1;
                Job nextJob = result.getJobAtSeqPos(nextJobPos);


                while(nToolsAdd != 0 & nextJob != null & diffPrevCurTools.size() > 0) {

                    ListIterator<Integer> iter = diffPrevCurTools.listIterator();

                    while (iter.hasNext() && nToolsAdd != 0) {
                        int toolAddId = iter.next();


                        if(result.getJobToolMatrix()[nextJob.getId()][toolAddId] == 1) {
                            result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
                            nToolsAdd-=1;
                            iter.remove();
                        }
                    }

                    nextJobPos = nextJobPos + 1;
                    nextJob = result.getJobAtSeqPos(nextJobPos);
                }



                //Fill remaining nToolsAdd with any tool
                ListIterator<Integer> remainingIter = diffPrevCurTools.listIterator();
                while(nToolsAdd != 0) {
                    int toolAddId = remainingIter.next();
                    result.getJobToolMatrix()[job.getId()][toolAddId] = 1;
                    remainingIter.remove();
                    nToolsAdd-=1;
                }

            }

        }


        //General.printGridP(result);
        //this.problemManager.getLogger().writeResult(result);
    }

    public LinkedList<Integer> getDifTools(int[] toolsA, int[] antiToolSetB) {

        LinkedList<Integer> list = new LinkedList<>();

        for(int i = 0; i < antiToolSetB.length; i++) {
            int toolId = antiToolSetB[i];
            if(toolsA[toolId] == 1) {
                list.add(toolId);
            }
        }
        return list;
    }

    public LinkedList<Integer> getDifTools(int[] toolsA, int[] antiToolSetB, int ktnsId) {

        LinkedList<Integer> list = new LinkedList<>();

        for(int i = 0; i < antiToolSetB.length; i++) {
            int toolId = antiToolSetB[i];
            if(toolsA[toolId] == 1 || toolsA[toolId] == ktnsId) {
                list.add(toolId);
            }
        }
        return list;
    }


    public int nSwitchesDelta(Result result) {

        //First job
        int setupCount = this.problemManager.getSETUP_MATRIX()[result.getJobIdAtSeqPos(0)][result.getJobIdAtSeqPos(0)];

        for (int seqPos = 1; seqPos < result.getSequence().length; seqPos++) {
            int prevJobId = result.getJobIdAtSeqPos(seqPos - 1);
            int jobId = result.getJobIdAtSeqPos(seqPos);
            setupCount+= this.problemManager.getSETUP_MATRIX()[prevJobId][jobId];
        }

        setupCount = setupCount - result.getnReductions();

        return setupCount - this.problemManager.getMAGAZINE_SIZE();
    }

    public int nSwitchesSetupBased(Result result) {
        //Inserstions
        int setupCount = 0;
        for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
            if (result.isToolUsedAtSeqPos(toolId,0)) {
                setupCount += 1;
            }
        }
        //Only check the tools that are d'office present...
        for (int seqPos = 1; seqPos < result.getSequence().length; seqPos++) {
            int swapCount = 0;

            for (int toolId = 0; toolId < this.problemManager.getN_TOOLS(); toolId++) {
                if (!result.isToolUsedAtSeqPos(toolId,seqPos - 1) && result.isToolUsedAtSeqPos(toolId, seqPos)) {
                    swapCount+=1;
                }
            }

            setupCount+= swapCount;
        }


        return  setupCount - this.problemManager.getMAGAZINE_SIZE();
    }




    public int nSwitches(int[] switches) {
        int count = 0;
        for (int i = 0; i < switches.length; i++) {
            count+= switches[i];
        }
        return count;
    }


    public int[] count_switches(Result result) {

        //Count first tool loadings

        int[] switches = new int[result.getSequence().length];

        Job jobPos1 = result.getJobAtSeqPos(0);

        //Inserstions
        int insertionCount = 0;
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if (result.getJobToolMatrix()[jobPos1.getId()][i] == 1) {
                insertionCount += 1;

            }
        }

        insertionCount = 0;
        switches[0] = insertionCount;

        for (int seqPos = 1; seqPos < result.getSequence().length; seqPos++) {
            int swapCount = 0;

            Job job = result.getJobAtSeqPos(seqPos);
            Job prevJob = result.getJobAtSeqPos(seqPos - 1);
            for (int j = 0; j < result.getJobToolMatrix()[job.getId()].length; j++) {
                if (result.getJobToolMatrix()[prevJob.getId()][j] ==  1 & result.getJobToolMatrix()[job.getId()][j] ==  0) {
                    swapCount+=1;
                    //System.out.println(job.getId() + "->" + j);
                }


                /*if (result.isToolUsedAtJobId(j,prevJob.getId()) && !result.isToolUsedAtJobId(j,job.getId())) {
                    swapCount+=1;
                }*/

            }
            switches[seqPos] = swapCount;
        }


        return switches;
    }







    public void KTNSGroundTruth(Result result) throws IOException {

        int[][] resultJobToolMatrix = new int[this.problemManager.getN_JOBS()][this.problemManager.getN_TOOLS()];

        int n = 0;

        int[] toolRowJob = new int[this.problemManager.getN_TOOLS()];

        int C = 0;

        //Step 1
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n, result) == 0) {
                toolRowJob[i] = 1;
                C+=1;
            }
            if(C == this.problemManager.getMAGAZINE_SIZE()) {
                break;
            }
        }

        //Step1.2
        n = 1;
        step2(n,toolRowJob,result);
    }


    public void step2(int n, int[] toolRowJob, Result result) throws IOException {
        if(n != this.problemManager.getN_JOBS()) {
            result.getJobToolMatrix()[n-1] = toolRowJob.clone();
            step3(n, toolRowJob,result);
        }else{
            stop(n, toolRowJob,result);
        }
    }

    //STEP 3 : If each i having L(i, n) = n also has Ji = 1, set n = n + 1 and go to Step 2.
    public void step3(int n, int[] toolRowJob , Result result) throws IOException {

        boolean goToStep2 = true;
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n,result) ==  n) {
                if(toolRowJob[i] != 1) {
                    goToStep2 = false;
                    break;
                }
            }
        }

        if(goToStep2) {
            n = n + 1;
            step2(n, toolRowJob, result);
        }else{
            step4(n, toolRowJob, result);
        }
    }



    //TODO: convertable to induvidual steps possible


    //Pick i having L(i, n) = n and Ji = 0. Set Ji = 1. IE INSERT THE REQUIRED TOOLS
    public void step4(int n, int[] toolRowJob, Result result) throws IOException {

        //Maybe for only 1 tool
        for (int i = 0; i < this.problemManager.getN_TOOLS(); i++) {
            if(L(i,n, result) == n & toolRowJob[i] == 0) {
                toolRowJob[i] = 1;
            }
        }
        step5(n, toolRowJob, result);
    }

    //Set Jk = 0 for a k that maximizes L(p, n) over {p: Jp = 1}. Go to Step 3. IE DELETE THE LEAST IMPORTANT TOOL

    public void step5(int n , int[] toolRowJob, Result result) throws IOException {

        //Step How many tools to remove?
        //Not explicitlely mentioned??
        int count = 0;
        for (int i = 0; i < toolRowJob.length; i++) {
            if(toolRowJob[i] ==  1) {
                count+=1;
            }
        }

        int nDelete = Math.max(0,count - this.problemManager.getMAGAZINE_SIZE());

        //TODO: possible to optimize

        while(nDelete != 0) {

            int maxL = -1;
            int id = -1;

            for (int i = 0; i < toolRowJob.length; i++) {
                if (toolRowJob[i] == 1) {
                    int value = L(i, n, result);
                    if (value > maxL) {
                        id = i;
                        maxL = value;
                    }
                }
            }

            if(id != -1) {
                //Set to Jk = O
                toolRowJob[id] = 0;
                nDelete--;
            }
        }



        step3(n, toolRowJob, result);
    }



    public void stop(int n, int[] toolRowJob, Result result) throws IOException {

    }




    public int L(int toolId, int n, Result result) {

        if(result.getSequence()[0] == 0) {
            //System.out.println("helloo");
        }
        for (int seqPos = n; seqPos < result.getSequence().length; seqPos++) {
            Job job = result.getJobAtSeqPos(seqPos);
            if(this.problemManager.getJOB_TOOL_MATRIX()[job.getId()][toolId] == 1) {
                return seqPos;
            }
        }

        return this.problemManager.getN_JOBS();
    }














    /* GETTERS & SETTERS ------------------------------------------------------------------ */


    public ProblemManager getProblemManager() {
        return problemManager;
    }

    public void setProblemManager(ProblemManager problemManager) {
        this.problemManager = problemManager;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
