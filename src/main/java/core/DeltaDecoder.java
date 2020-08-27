package core;

import data_processing.Parameters;
import models.elemental.Job;

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
    }



    public void KTNS(Result result){

        result.setKtnsId(result.getKtnsId() + 1);
        int ktnsSuccess = 0;

        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobAtSeqPos(seqPos);

            if (seqPos != 0) {
                int prevJobId = result.getJobIdAtSeqPos(seqPos - 1);
                int nToolsAdd = Math.max(0,this.problemManager.getMAGAZINE_SIZE() - job.getSet().length);
                int nextJobSeqPos = seqPos + 1;
                int nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);

                while(nToolsAdd != 0 & nextJobId != -1) {
                    for(int toolId: job.getAntiSet()) {
                        if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId()) && result.isToolUsedAtJobId(toolId, nextJobId)) {
                            result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                            nToolsAdd--;
                        }
                    }

                    nextJobSeqPos = nextJobSeqPos + 1;
                    nextJobId = result.getJobIdAtSeqPos(nextJobSeqPos);
                }

                while(nToolsAdd != 0) {
                    for(int toolId: job.getAntiSet()) {
                        if(result.isToolUsedAtJobId(toolId,prevJobId) && !result.isToolKTNSAtJobId(toolId, job.getId())) {
                            result.getJobToolMatrix()[job.getId()][toolId] = result.getKtnsId();
                            nToolsAdd--;
                        }
                    }
                }

            }
        }
    }


    public void evaluate(DecodeFeedback decodeFeedback) {


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
