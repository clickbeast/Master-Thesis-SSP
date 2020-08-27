package core;

import data_processing.Parameters;
import models.elemental.Job;
import util.General;

import java.io.IOException;
import java.util.LinkedList;


public class DeltaDecoder {


    ProblemManager problemManager;
    private Parameters parameters;

    class DecodeFeedback {
        ResultDelta result;
        int ktnsSuccess = 0;
    }


    public DeltaDecoder(ProblemManager problemManager, Parameters parameters) {
        this.problemManager = problemManager;
        this.parameters = parameters;
    }


    public void decode(Result result) throws IOException {
        this.KTNS(result);
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

                //Pool every tool in thread

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



    public void evaluate(Result result) {
        result.setnSwitches(nSwitchesSetupBased(result));
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
