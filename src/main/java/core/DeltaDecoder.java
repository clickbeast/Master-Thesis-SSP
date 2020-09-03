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

        //this.KTNS(result);

        this.KTNSHalf(result);
        //this.KTNSVerified(result);
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
