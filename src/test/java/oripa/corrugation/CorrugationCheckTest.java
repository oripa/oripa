package oripa.corrugation;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;

import org.junit.Test;
import oripa.corrugation.CorrugationChecker;
import oripa.doc.Doc;
import oripa.doc.LoadingDoc;
import oripa.doc.loader.LoaderXML;
import oripa.file.FileVersionError;
import oripa.fold.OrigamiModel;
import oripa.fold.OrigamiModelFactory;
import oripa.paint.creasepattern.CreasePattern;
import oripa.value.OriLine;

public class CorrugationCheckTest {

    private CorrugationChecker checker; 

    private OrigamiModel getModelFromFilename(String filename){
        Doc doc;
        OrigamiModelFactory modelFactory = new OrigamiModelFactory();
        LoaderXML loaderXML = new LoaderXML();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String path = cl.getResource(filename).getFile();
        try{
            doc = loaderXML.load(path);
        }
        catch(FileVersionError err){
            return null;
        }

        return modelFactory.createOrigamiModel3(doc.getCreasePattern(), doc.getPaperSize(), true);

    }

    @Test
    public void testMiuraOri() {
    	checker = new CorrugationChecker();
        System.out.println("testMiuraOri");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/miura-ori.opx")));
    }

    @Test
    public void testBadVertexCondition() {     
    	checker = new CorrugationChecker();
        System.out.println("testBadVertexCondition");
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/badvertexcondition.opx")));
    }

    @Test
    public void testCraneBase() {
    	checker = new CorrugationChecker();
        System.out.println("testCraneBase");
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/crane-base.opx")));
    }

    @Test
    public void testRadial() {
    	checker = new CorrugationChecker();
        System.out.println("testRadial");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/radial.opx")));
    }

    @Test
    public void test90Same() {
    	checker = new CorrugationChecker();
        System.out.println("test90Same");
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/90-same.opx")));
    }

    @Test
    public void test90Valid() {
    	checker = new CorrugationChecker();
        System.out.println("test90Valid");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/90-valid.opx")));
    }

    @Test
    public void test90ValidReverse() {
    	checker = new CorrugationChecker();
        System.out.println("test90ValidReverse");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/90-valid-reverse.opx")));
    }

    @Test
    public void test90InvalidFace() {
    	checker = new CorrugationChecker();
        System.out.println("test90InvalidFace");
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/90-invalid-face.opx")));
    }

    @Test
    public void testDegree4() {
        checker = new CorrugationChecker();
        System.out.println("testDegree4");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/degree-4.opx")));
    }

    @Test
    public void test2x2IsoArea() {
        checker = new CorrugationChecker();
        System.out.println("test2x2IsoArea");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/2x2isoarea.opx")));
    }

    @Test
    public void testEquidistantWeave() {
    	checker = new CorrugationChecker();
        System.out.println("testEquidistantWeave");
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/equidistant-weave.opx")));
    }

}
