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
        this.KTNSVerified(result);
        this.evaluate(result);
    }

    public void KTNS(Result result){

        result.setKtnsId(result.getKtnsId() + 1);

        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobAtSeqPos(seqPos);

            if (seqPos != 0) {
                int prevJobId = result.getJobIdAtSeqPos(seqPos - 1);
                int nToolsAdd = Math.max(0,this.problemManager.getMAGAZINE_SIZE() - job.getSet().length);
                int nextJobSeqPos = seqPos + 1;
                int nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);

                while(nToolsAdd != 0 & nextJobId != -1) {
                    for(int toolId: job.getAntiSet()) {

                        //System.out.println(result.isToolUsedAtJobId(toolId,prevJobId));
                        //System.out.println(result.isToolKTNSAtJobId(toolId, job.getId()));
                        //System.out.println(result.isToolUsedAtJobId(toolId, nextJobId));
                        if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId()) && result.isToolUsedAtJobId(toolId, nextJobId)) {
                            result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                            nToolsAdd--;
                        }
                    }
                    //System.out.println(nextJobId);

                    nextJobSeqPos = nextJobSeqPos + 1;
                    nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);
                }

                for(int toolId: job.getAntiSet()) {
                    if(nToolsAdd == 0) {
                        break;
                    }

                    if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId())) {
                        result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                        nToolsAdd--;
                    }
                }
            }
        }

        //General.printGridP(result);
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

        this.evaluate(result);
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




    public void evaluate(Result result) {
        result.setnSwitches(nSwitches(count_switches(result)));
        result.setCost((double) result.getnSwitches());
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
            for (int toolId = 0; toolId < this.problemManager.getN_JOBS(); toolId++) {
                //CHECK: current implementation: when a tool gets loaded a "switch" is performed
                if (!result.isToolUsedAtSeqPos(toolId,seqPos - 1) && result.isToolUsedAtSeqPos(toolId, seqPos)) {
                    swapCount+=1;
                }
            }
            setupCount+=swapCount;
        }
        //System.out.println(setupCount);
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
                }
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
