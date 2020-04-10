package core;

public class SequencePosition {

    private SequencePosition previousPosition;
    private ProblemManager problemManager;

    private int jobId;

    private int switches;
    private int toolAddDistance;
    private int toolRemoveDistance;

    private int cumulativeSwitches;
    private int cumulativeToolAddDistance;
    private int cumulativeToolRemoveDistance;

    private int[] tools;
    private int[] requiredTools;
    private int[] addedTools;
    private int[] removedTools;


    public SequencePosition() {

    }



    public int calculateSwitches() {

        return 0;
    }

    public int calculateToolAddDistance() {

        return 0;
    }

    public int calculateToolDeleteDistance() {

        return 0;
    }


}
