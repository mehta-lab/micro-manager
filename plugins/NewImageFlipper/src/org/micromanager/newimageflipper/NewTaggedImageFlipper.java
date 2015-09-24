/*
 * AUTHOR:
 * Arthur Edelstein, Nico Stuurman
 *
 * Copyright (c) 2011, 2012 Regents of the University of California
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

package org.micromanager.newimageflipper;

import org.micromanager.api.MMProcessorPlugin;

/**
 * Example demonstrating the use of DataProcessors.  DataProcessors can 
 * get hold of images coming out of the acquisition engine before they 
 * are inserted into the ImageCache.  DataProcessors can modify images 
 * or even generate totally new ones.
 * 
 * 
 * @author arthur
 */
public class NewTaggedImageFlipper implements MMProcessorPlugin {
   public static String menuName = "Image Flipper";
   public static String tooltipDescription = "Mirrors, flips and rotates images on the fly";
  
   public static Class<?> getProcessorClass() {
      return NewImageFlippingProcessor.class;
   }

   public void configurationChanged() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public String getDescription() {
      return "Rotates and/or mirrors images coming from the selected camera";
   }

   @Override
   public String getInfo() {
      return "Not supported yet.";
   }

   @Override
   public String getVersion() {
      return "Version 0.2";
   }

   @Override
   public String getCopyright() {
      return "Copyright University of California San Francisco, 2014";
   }

}
