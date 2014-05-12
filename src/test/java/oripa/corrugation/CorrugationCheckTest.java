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
        CorrugationChecker checker = new CorrugationChecker();
        assertFalse(checker.evaluate(getModelFromFilename("corrugation/badvertexcondition.opx")));
    }

}
