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

    private CorrugationChecker checker = new CorrugationChecker();

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
        CorrugationChecker checker = new CorrugationChecker();
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/miura-ori.opx")));
    }

    @Test
    public void testBadVertexCondition() {        
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/badvertexcondition.opx")));
    }

    @Test
    public void testCraneBase() {
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/crane-base.opx")));
    }

    @Test
    public void testRadial() {
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/radial.opx")));
    }

    @Test
    public void test90same() {
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/90-same.opx")));
    }

    @Test
    public void test90Valid() {
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/90-valid.opx")));
    }

    @Test
    public void test90ValidReverse() {
        assertTrue(checker.evaluate(getModelFromFilename("corrugation/90-valid-reverse.opx")));
    }

    @Test
    public void test90InvalidFace() {
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/90-invalid-face.opx")));
    }

}
