package core;

import data_processing.Parameters;
import models.elemental.Job;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;


public class DeltaDecoder {


    ProblemManager problemManager;
    private Parameters parameters;

    class DecodeFeedback {
        Result result;
        int ktnsSuccess = 0;
    }


    public DeltaDecoder(ProblemManager problemManager, Parameters parameters) {
        this.problemManager = problemManager;
        this.parameters = parameters;
    }


    public void decode(Result result) throws IOException {
        this.KTNS(result);
    }



    public void KTNS(Result result) {
        result.setKtnsId(result.getKtnsId() + 1);

        for (int seqPos = 0; seqPos < result.getSequence().length; seqPos++) {

            Job job = result.getJobAtSeqPos(seqPos);

            //base case
            if (seqPos != 0) {

                Job prevJob = result.getJobAtSeqPos(seqPos - 1);

                LinkedList<Integer> diffPrevCurTools = this.getDifTools(result.getJobToolMatrix()[prevJob.getId()], job.getAntiSet());

                int unionPrevCurJobSize = diffPrevCurTools.size() + job.getSet().length;
                int nToolsDelete = Math.max(0, unionPrevCurJobSize - this.problemManager.getMAGAZINE_SIZE());
                int nToolsKeep = unionPrevCurJobSize - nToolsDelete;
                int nToolsAdd = nToolsKeep - job.getSet().length;


                Job nextJob = result.nextJob(job);

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

                    nextJob = result.nextJob(nextJob);
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


    public void evaluate(DecodeFeedback decodeFeedback) {


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
