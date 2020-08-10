package de.templum.routeplaner.computing.genetic;



class Parents {

    private final Individual mMother;
    private final Individual mFather;

    Parents(Individual mother, Individual father) {
        mMother = mother;
        mFather = father;
    }

    Individual reproduce() {
        return mMother.orderedCrossOver(mFather);
    }
}
