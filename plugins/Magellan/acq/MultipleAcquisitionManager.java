/*
 * AUTHOR:
 * Henry Pinkard, henry.pinkard@gmail.com
 *
 * Copyright (c) 2015 Regents of the University of California
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

package acq;

import gui.GUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import javax.swing.JOptionPane;
import misc.Log;

/**
 *
 * @author Henry
 */
public class MultipleAcquisitionManager {
   
   private ArrayList<FixedAreaAcquisitionSettings> acqSettingsList_ = new ArrayList<FixedAreaAcquisitionSettings>();
   private ArrayList<Integer> numberInGroup_ = new ArrayList<Integer>();
   private String[] acqStatus_;
   private GUI gui_;
   private MagellanEngine eng_;
   private volatile boolean running_ = false;
   private Thread managerThread_;
   private volatile ParallelAcquisitionGroup currentAcqs_;
   private CyclicBarrier acqGroupFinishedBarrier_ = new CyclicBarrier(2);
   
   public MultipleAcquisitionManager(GUI gui, MagellanEngine eng ) {
      gui_ = gui;
      acqSettingsList_.add(new FixedAreaAcquisitionSettings());
      eng_ = eng;
      eng_.setMultiAcqManager(this);
      numberInGroup_.add(1);
   }
      
   public FixedAreaAcquisitionSettings getAcquisitionSettings(int index) {
      return acqSettingsList_.get(index);
   }
   
   public int getSize() {
      return acqSettingsList_.size();
   }
   
   public String getAcquisitionName(int index) {
      return acqSettingsList_.get(index).name_;
   }
   
   /**
    * @return change in position of selected acq
    */
   public int moveUp(int index) {
      if (index == 0) {
         //nothing to do
         return 0;
      } else if (getIndexInGroup(index) != 0) {
         //if its within group, move up within group      
         acqSettingsList_.add(index - 1, acqSettingsList_.remove(index));
         return -1;
      } else {
         //move this group above entire group above
         int groupIndex = getGroupIndex(index);
         int insertIndex = getFirstIndexOfGroup(groupIndex - 1);
         //extract index should be last one in this group
         int extractIndex = getFirstIndexOfGroup(groupIndex) + getGroupSize(index) - 1;
         //remove in reverse order and readd to new position
         int groupSize = getGroupSize(index);
         for (int i = 0; i < groupSize; i++) {
            acqSettingsList_.add(insertIndex, acqSettingsList_.remove(extractIndex));
         }
         //swap num in group with one above
         numberInGroup_.add(groupIndex - 1, numberInGroup_.remove(groupIndex));
         return insertIndex - index;
      }
   }
   
   public int moveDown(int index) {
      if (index == acqSettingsList_.size() - 1) {
         //nothing to do
         return 0;
      } else if (getIndexInGroup(index) != getGroupSize(index) - 1) {
         //if its within group, move down within group      
         acqSettingsList_.add(index + 1, acqSettingsList_.remove(index));
         return 1;
      } else {
         //move group below above this group   
         int groupIndex = getGroupIndex(index);
         int insertIndex = getFirstIndexOfGroup(getGroupIndex(index) );
         int extractIndex = getFirstIndexOfGroup(getGroupIndex(index) + 1) + numberInGroup_.get(getGroupIndex(index) + 1) - 1;
         //remove in reverse order and readd to new position
         int groupSize = numberInGroup_.get(getGroupIndex(index) + 1);
         for (int i = 0; i < groupSize; i++) {
            acqSettingsList_.add(insertIndex, acqSettingsList_.remove(extractIndex));
         }     
         //swap num in group below with this one 
         numberInGroup_.add(groupIndex, numberInGroup_.remove(groupIndex + 1));
         return - index + extractIndex;
      }
   }
   
   public void addNew() {
      acqSettingsList_.add(new FixedAreaAcquisitionSettings());
            numberInGroup_.add(1);
   }
   
   public void remove(int index) {
      //must always have at least one acquisition
      if (index != -1 && acqSettingsList_.size() > 1) {
         acqSettingsList_.remove(index);
         int groupIndex = getGroupIndex(index);
         if (numberInGroup_.get(groupIndex) == 1) {
            numberInGroup_.remove(groupIndex);
         } else {
            numberInGroup_.add(groupIndex,numberInGroup_.remove(groupIndex) - 1);
         }
      }
   }
   
   public int getGroupIndex(int acqIndex) {
      int groupIndex = 0;
      int sum = -1;
      for (Integer i : numberInGroup_) {
         sum += i;
         if (acqIndex <= sum) {
            return groupIndex;
         }
         groupIndex++;
      }
      //shouldn't ever happen
      throw new RuntimeException();
   }
   
   public int getIndexInGroup(int index) {
      if (getGroupIndex(index) == 0) {
         return index;
      } else {
         int groupIndex = getGroupIndex(index);
         int firstInGroup = index;
         while (getGroupIndex(firstInGroup - 1) == groupIndex) {
            firstInGroup--;
         }
         return index - firstInGroup;
      }
   }
   
   public int getFirstIndexOfGroup(int groupIndex) {
      for (int i = 0; i < acqSettingsList_.size(); i++) {
         if (getGroupIndex(i) == groupIndex) {
            return i;
         }
      }
      return numberInGroup_.size();
   }
   
   public int getGroupSize(int index) {
      return numberInGroup_.get(getGroupIndex(index));
   }
   
   public void addToParallelGrouping(int index) {
      //make sure there is one below 
      if (index != acqSettingsList_.size() - 1) {
         //if one below is in same group, try again with one below that
         if (getGroupIndex(index) == getGroupIndex(index+1)) {
            addToParallelGrouping(index + 1);
            return;
         }
         //group em
         int gIndex = getGroupIndex(index);
         numberInGroup_.add(gIndex,numberInGroup_.remove(gIndex) + numberInGroup_.remove(gIndex));      
      }
   }

   public void removeFromParallelGrouping(int index) {
      int groupIndex = getGroupIndex(index);
      int groupSize = getGroupSize(index);
      if (groupSize != 1) {
         //if theyre actually grouped to begin with, remove this one from group
         //move higher or lower depending on position in group
         //find position in group and see if its on top or bottom half

         if (getIndexInGroup(index) > groupSize / 2.0) {
            //move down
            numberInGroup_.add(groupIndex, numberInGroup_.remove(groupIndex) - 1);
            numberInGroup_.add(groupIndex+1,1);
            acqSettingsList_.add(getFirstIndexOfGroup(groupIndex+1), acqSettingsList_.remove(index));
         } else {
            //move up
            numberInGroup_.add(groupIndex, numberInGroup_.remove(groupIndex) - 1);
            numberInGroup_.add(groupIndex,1);
            acqSettingsList_.add(getFirstIndexOfGroup(groupIndex), acqSettingsList_.remove(index));
         }
      } 
   }
   
   public String getAcqStatus(int index) {
      if (acqStatus_ == null || index >= acqStatus_.length) {
         return "";
      }
      return acqStatus_[index];
   }
   
   public boolean isRunning() {
      return running_;
   }
   
   public void abort() {
      int result = JOptionPane.showConfirmDialog(null, "Abort current acquisition and cancel future ones?", "Finish acquisitions?", JOptionPane.OK_CANCEL_OPTION);
      if (result != JOptionPane.OK_OPTION) {
         return;
      }
      
      //stop future acquisitions
      managerThread_.interrupt();
      //abort current parallel acquisition group
      if (currentAcqs_ != null) {
         currentAcqs_.abort();
      }      
      //abort blocks until all the acquisition stuff is closed, so can reset GUI here
         multipleAcquisitionsFinsihed();
   }

   public void runAllAcquisitions() {
     managerThread_ = new Thread(new Runnable() {
         @Override
         public void run() {                
            gui_.enableMultiAcquisitionControls(false); //disallow changes while running
            running_ = true;
            acqStatus_ = new String[acqSettingsList_.size()];
            Arrays.fill(acqStatus_, "Waiting");
            gui_.repaint();
            //run acquisitions
            for (int groupIndex = 0; groupIndex < numberInGroup_.size(); groupIndex++) {
               if (managerThread_.isInterrupted()) {
                  break; //user aborted
               }
               //mark all in parallel group as running
               for (int i = 0; i < numberInGroup_.get(groupIndex); i++) {
                  acqStatus_[getFirstIndexOfGroup(groupIndex) + i] = "Running";
                  gui_.repaint();
               }
               //run one or more acquisitions in parallel group 
               try {
                  currentAcqs_ = eng_.runInterleavedAcquisitions(acqSettingsList_.subList(
                          getFirstIndexOfGroup(groupIndex), getFirstIndexOfGroup(groupIndex) + getGroupSize(getFirstIndexOfGroup(groupIndex))), true);
               } catch (Exception e) {
                  //abort all if any has an error
                  multipleAcquisitionsFinsihed();
                  return;
               }
               if (currentAcqs_ == null) {
                  //user has responded to dialog and doesnt want to interupt currently running ones
                  break;
               }   
               try {
                  acqGroupFinishedBarrier_.await();
               } catch (Exception ex) {
                  //all multiple acquisitions aborted
                  break;
               }
               
               //mark as finished, unless already marked as aborted
               for (int i = 0; i < numberInGroup_.get(groupIndex); i++) {
                  if (!getAcqStatus( getFirstIndexOfGroup(groupIndex) + i).equals("Aborted") ) {
                     acqStatus_[getFirstIndexOfGroup(groupIndex) + i] = "Finished";
                  }
               }
               gui_.repaint();
            }          
            multipleAcquisitionsFinsihed();          
         }
      }, "Multiple acquisition manager thread");
     managerThread_.start();
   }
   
   private void multipleAcquisitionsFinsihed() {
      //reset barier for a new round
      acqGroupFinishedBarrier_ = new CyclicBarrier(2);
      //update GUI
      running_ = false;
      acqStatus_ = null;
      gui_.enableMultiAcquisitionControls(true);
   }
   
   public void markAsAborted(FixedAreaAcquisitionSettings settings) {
      if (acqStatus_ != null) {
         acqStatus_[acqSettingsList_.indexOf(settings)] = "Aborted";
         gui_.repaint();
      }
   }
   
   /**
    * Called by parallel acquisition group when it is finished so that manager knows to move onto next one
    */
   public void parallelAcqGroupFinished() {
      try {
         if (managerThread_.isAlive()) {
            acqGroupFinishedBarrier_.await();
         } //otherwise it was aborted, so nothing to do        
      } catch (Exception ex) {
         //exceptions should never happen because this is always the second await to be called
         Log.log("Unexpected exception: multi acq manager interrupted or barrier broken");
         ex.printStackTrace();
         throw new RuntimeException();
      }
      currentAcqs_ = null;
   }
   
}
