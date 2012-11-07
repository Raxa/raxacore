/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.raxa.module.raxacore.impl;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.raxa.module.raxacore.Image;
import org.raxa.module.raxacore.db.ImageDAO;

/**
 *
 * @author joman
 */
public class ImageServiceImplTest {
    
    public ImageServiceImplTest() {
    }

    /**
     * Test of setImageDAO method, of class ImageServiceImpl.
     */
    @Test
    public void testSetImageDAO() {
        System.out.println("setImageDAO");
        ImageDAO dao = null;
        ImageServiceImpl instance = new ImageServiceImpl();
        instance.setImageDAO(dao);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of saveImage method, of class ImageServiceImpl.
     */
    @Test
    public void testSaveImage() {
        System.out.println("saveImage");
        Image image = null;
        ImageServiceImpl instance = new ImageServiceImpl();
        Image expResult = null;
        Image result = instance.saveImage(image);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImageByUuid method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImageByUuid() {
        System.out.println("getImageByUuid");
        String uuid = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        Image expResult = null;
        Image result = instance.getImageByUuid(uuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllImages method, of class ImageServiceImpl.
     */
    @Test
    public void testGetAllImages() {
        System.out.println("getAllImages");
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getAllImages();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImagesByName method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImagesByName() {
        System.out.println("getImagesByName");
        String name = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getImagesByName(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateImage method, of class ImageServiceImpl.
     */
    @Test
    public void testUpdateImage() {
        System.out.println("updateImage");
        Image image = null;
        ImageServiceImpl instance = new ImageServiceImpl();
        Image expResult = null;
        Image result = instance.updateImage(image);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteImage method, of class ImageServiceImpl.
     */
    @Test
    public void testDeleteImage() {
        System.out.println("deleteImage");
        Image image = null;
        ImageServiceImpl instance = new ImageServiceImpl();
        instance.deleteImage(image);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImagesByProviderUuid method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImagesByProviderUuid() {
        System.out.println("getImagesByProviderUuid");
        String providerUuid = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getImagesByProviderUuid(providerUuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImagesByPatientUuid method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImagesByPatientUuid() {
        System.out.println("getImagesByPatientUuid");
        String patientUuid = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getImagesByPatientUuid(patientUuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImagesByLocationUuid method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImagesByLocationUuid() {
        System.out.println("getImagesByLocationUuid");
        String locationUuid = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getImagesByLocationUuid(locationUuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getImagesByTag method, of class ImageServiceImpl.
     */
    @Test
    public void testGetImagesByTag() {
        System.out.println("getImagesByTag");
        String tag = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        List expResult = null;
        List result = instance.getImagesByTag(tag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLatestImageByTag method, of class ImageServiceImpl.
     */
    @Test
    public void testGetLatestImageByTag() {
        System.out.println("getLatestImageByTag");
        String tag = "";
        ImageServiceImpl instance = new ImageServiceImpl();
        Image expResult = null;
        Image result = instance.getLatestImageByTag(tag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onStartup method, of class ImageServiceImpl.
     */
    @Test
    public void testOnStartup() {
        System.out.println("onStartup");
        ImageServiceImpl instance = new ImageServiceImpl();
        instance.onStartup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onShutdown method, of class ImageServiceImpl.
     */
    @Test
    public void testOnShutdown() {
        System.out.println("onShutdown");
        ImageServiceImpl instance = new ImageServiceImpl();
        instance.onShutdown();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPath method, of class ImageServiceImpl.
     */
    @Test
    public void testGetPath() {
        System.out.println("getPath");
        Image p = null;
        ImageServiceImpl instance = new ImageServiceImpl();
        String expResult = "";
        String result = instance.getPath(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
