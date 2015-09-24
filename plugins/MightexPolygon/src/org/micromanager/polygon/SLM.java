/*
 * Mightex Polygon400 plugin.
 *
 * AUTHOR:
 * Wayne Liao, mightexsystem.com, 05/15/2015
 *
 * Copyright (c) 2015 Mightex Systems
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.micromanager.polygon;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.logging.Level;
import java.util.logging.Logger;
import mmcorej.CMMCore;
import org.micromanager.utils.ReportingUtils;

/**
 *
 * @author Wayne
 */
public class SLM {
    private final CMMCore mmc_;
    private String slm_;
    private int slmWidth_;
    private int slmHeight_;
    private int slmMaxSeq_;
    
    public SLM(CMMCore mmc)
    {
        mmc_ = mmc;
        try{
            slm_ = mmc_.getSLMDevice();
            Utility.LogMsg("SLM Type: " + mmc_.getDeviceType(slm_));
            Utility.LogMsg("SLM Description: " + mmc_.getDeviceDescription(slm_));
            Utility.LogMsg("SLM Name: " + mmc_.getDeviceName(slm_));
            Utility.LogMsg("SLM Library: " + mmc_.getDeviceLibrary(slm_));
            slmWidth_ = (int)mmc_.getSLMWidth(slm_);
            slmHeight_ = (int)mmc_.getSLMHeight(slm_);
            slmMaxSeq_ = (int)mmc_.getSLMSequenceMaxLength(slm_);
        }
        catch(Exception ex){
            Utility.LogMsg("SLM.SLM: " + ex.toString() );
            ReportingUtils.logError(ex);
        }
    }

    public int getWidth() 
    {
        return slmWidth_;
    }
    public int getHeight() 
    {
        return slmHeight_;
    }
    public int getMaxLength()
    {
        return slmMaxSeq_;
    }
    public void setExposureTime( double ms )
    {
        try {
            mmc_.setSLMExposure(slm_, ms);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    public void commandTrigger()
    {
        try {
            mmc_.setProperty(slm_, "CommandTrigger", 0);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    public void setTriggerType( int n )
    {
        try {
            mmc_.setProperty(slm_, "TriggerType", n);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
  
    public void loadSequence(java.util.List<byte[]> sequence)
    {
        try {
            mmc_.loadSLMSequence(slm_, sequence);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    public void startSequence()
    {
        try {
            mmc_.startSLMSequence(slm_);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    public void stopSequence()
    {
        try {
            mmc_.stopSLMSequence(slm_);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    
    public void ShowGrayscale(int n)
    {
        try {
            mmc_.setSLMPixelsTo(slm_, (byte)n);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }

    public void ShowBlank()
    {
        ShowGrayscale((byte)0);
    }
    
    public void ShowSpot(int x, int y, int r)
    {
        ImageProcessor proc = new ByteProcessor(slmWidth_, slmHeight_);
        proc.setColor(Color.black);
        proc.fill();
        proc.setColor(Color.white);
        proc.fillOval((int) (x - r), (int) (y - r), r * 2, r * 2);
        try {
            mmc_.setSLMImage(slm_, (byte[]) proc.getPixels());
            mmc_.displaySLMImage(slm_);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    
    public void ShowGrid()
    {
        final int cols = 8, rows = 8;
        ImageProcessor proc = new ByteProcessor(slmWidth_, slmHeight_);
        proc.setColor(Color.black);
        proc.fill();
        proc.setColor(Color.white);
        for(int i = 0; i <= cols; i++){
            int x = ( slmWidth_ - 1 ) * i / cols;
            proc.moveTo( x, 0 );
            proc.lineTo( x, slmHeight_ - 1);
        }
        for(int i = 0; i <= rows; i++){
            int y = ( slmHeight_ - 1 ) * i / rows;
            proc.moveTo( 0, y );
            proc.lineTo( slmWidth_ - 1, y );
        }
        proc.moveTo( 0, 0 );
        proc.lineTo( slmWidth_ * 2 / cols, slmHeight_ / rows);
        Polygon plg = new Polygon(new int[] { 0, ( slmWidth_ - 1 ) * 2 / cols, ( slmWidth_ - 1 ) * 2 / cols, 0 }, new int[] { 0, 0, ( slmHeight_ - 1 ) / rows, ( slmHeight_ - 1 ) / rows }, 4);
        proc.fillPolygon( plg );
//        Utility.Save(proc, "grid.bmp");
        try {
            mmc_.setSLMImage(slm_, (byte[]) proc.getPixels());
            mmc_.displaySLMImage(slm_);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    
    public void ShowImage( BufferedImage bi )
    {
        DataBufferByte dbb = (DataBufferByte)bi.getRaster().getDataBuffer();
        try {
            mmc_.setSLMImage( slm_, dbb.getData() );
            mmc_.displaySLMImage(slm_);
        } catch (Exception ex) {
            ReportingUtils.showError(ex);
        }
    }
    
    public void SaveAffineTransform( AffineTransform at )
    {
        try {
            mmc_.setProperty(slm_, "AffineTransform.m00", at.getScaleX());
            mmc_.setProperty(slm_, "AffineTransform.m01", at.getShearX());
            mmc_.setProperty(slm_, "AffineTransform.m02", at.getTranslateX());
            mmc_.setProperty(slm_, "AffineTransform.m10", at.getShearY());
            mmc_.setProperty(slm_, "AffineTransform.m11", at.getScaleY());
            mmc_.setProperty(slm_, "AffineTransform.m12", at.getTranslateY());
        } catch (Exception ex) {
            Logger.getLogger(SLM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
