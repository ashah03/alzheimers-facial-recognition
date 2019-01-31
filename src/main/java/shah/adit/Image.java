package shah.adit;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.*;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.experiment.dataset.*;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.model.EigenImages;
import org.openimaj.image.processing.face.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")

public class Image {

    public static void main(String args[]) {
        MBFImage image = null;

        //Load dataset
        VFSGroupDataset<FImage> dataset = null;
        try {
            dataset =
                    new VFSGroupDataset<FImage>("file://Users/adit/Downloads/att_faces", ImageUtilities.FIMAGE_READER);
            //new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
        } catch (FileSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Split dataset into halves: One for training and one for testing
        int nTraining = 5;
        int nTesting = 5;
        GroupedRandomSplitter<String, FImage> splits =
                new GroupedRandomSplitter<String, FImage>(dataset, nTraining, 0, nTesting);
        GroupedDataset<String, ListDataset<FImage>, FImage> training = splits.getTrainingDataset();
        GroupedDataset<String, ListDataset<FImage>, FImage> testing = splits.getTestDataset();
        //Train Eigenfaces images
        List<FImage> basisImages = DatasetAdaptors.asList(training);
        int nEigenvectors = 100;
        EigenImages eigen = new EigenImages(nEigenvectors);
        eigen.train(basisImages);
        //Draws each Eigen face
        List<FImage> eigenFaces = new ArrayList<FImage>();
        for (int i = 0; i < 12; i++) {
            eigenFaces.add(eigen.visualisePC(i));
        }
        DisplayUtilities.display("EigenFaces", eigenFaces);
        //Build a database of features using Map of Strings
        Map<String, DoubleFV[]> features = new HashMap<String, DoubleFV[]>();
        for (final String person : training.getGroups()) {
            final DoubleFV[] fvs = new DoubleFV[nTraining];

            for (int i = 0; i < nTraining; i++) {
                final FImage face = training.get(person).get(i);
                fvs[i] = eigen.extractFeature(face);
            }
            features.put(person, fvs);
        }
        //Extract feature from each image and find smallest distance, return identifier of corresponding person

        double correct = 0, incorrect = 0;
        for (String truePerson : testing.getGroups()) {
            for (FImage face : testing.get(truePerson)) {
                DoubleFV testFeature = eigen.extractFeature(face);

                String bestPerson = null;
                double minDistance = Double.MAX_VALUE;
                for (final String person : features.keySet()) {
                    for (final DoubleFV fv : features.get(person)) {
                        double distance = fv.compare(testFeature, DoubleFVComparison.EUCLIDEAN);
                        //determine best person
                        if (distance < minDistance) {
                            minDistance = distance;
                            bestPerson = person;
                        }
                    }
                }
//Print actual image and guessed image
                System.out.println("Actual: " + truePerson + "\tguess: " + bestPerson);
                //Calculate # of correct and incorrect guesses
                if (truePerson.equals(bestPerson))
                    correct++;
                else
                    incorrect++;
            }
        }
//print accuracy
        System.out.println("Accuracy: " + (correct / (correct + incorrect)));
    }


}


