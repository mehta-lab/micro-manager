/*
 * AUTHOR:
 * Nenad Amodaj, nenad@amodaj.com, November 10, 2005
 *
 * Copyright (c) 2006 Regents of the University of California
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

package channels;
import java.awt.Color; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Channel acquisition protocol. 
 */
public class ChannelSetting {

   final public String group_;
   final public String config_; // Configuration setting name
   final public String name_; 
   public double exposure_; // ms
   public Color color_;
   public boolean use_ = true;
   final public boolean uniqueEvent_;

   public ChannelSetting(String group, String config, String name, double exposure, Color color, boolean use, boolean uniqueEvent) {
      group_ = group;
      color_ = color;
      config_ = config;
      name_ = name;
      exposure_ = exposure;
      use_ = use;
      uniqueEvent_ = uniqueEvent; // true for only first on multichannel camera
   }

   public ChannelSetting copy() {
      return new ChannelSetting(group_, config_, name_, exposure_, new Color(color_.getRGB()), use_, uniqueEvent_);
   }

   /**
    * Serialize to JSON encoded string
    */
   public static String toJSONStream(ChannelSetting cs) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(cs);
   }
   
   /**
    * De-serialize from JSON encoded string
    */
   public static ChannelSetting fromJSONStream(String stream) {
      Gson gson = new Gson();
      ChannelSetting cs = gson.fromJson(stream, ChannelSetting.class);
      return cs;
   }
   
//   // test serialization
//   public synchronized static void main(String[] args) {
//      
//      // encode
//      ChannelSettings cs = new ChannelSettings();
//      String stream = ChannelSettings.toJSONStream(cs);
//      System.out.println("Encoded:\n" + stream);
//      
//      // decode
//      ChannelSettings resultCs = ChannelSettings.fromJSONStream(stream);
//      System.out.println("Decoded:\n" + ChannelSettings.toJSONStream(resultCs));
//   }
//   
}

