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

import acq.MultipleAcquisitionManager;
import gui.GUI;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Henry
 */
public class MultipleAcquisitionTableModel extends AbstractTableModel {

   private static final String[] COLUMNS = {"Order","Name","Status"};
   private MultipleAcquisitionManager manager_;
   private GUI gui_;
   
   public MultipleAcquisitionTableModel(MultipleAcquisitionManager manager, GUI gui) {
      super();
      manager_ = manager;
      gui_ = gui;
   }
   
   @Override
   public String getColumnName(int index) {
      return COLUMNS[index];
   }

   @Override
   public int getRowCount() {
      return manager_.getSize();
   }

   @Override
   public int getColumnCount() {
      return COLUMNS.length;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      if (columnIndex == 0) {
         return manager_.getGroupIndex(rowIndex) + 1;
      } else if (columnIndex == 1) {
         return manager_.getAcquisitionName(rowIndex);
      } else {
         return manager_.getAcqStatus(rowIndex);
      }
   }

   @Override
   public void setValueAt(Object value, int row, int col) {
       if (col == 1) {
         manager_.getAcquisitionSettings(row).name_ = (String) value;
         gui_.refreshAcquisitionSettings(); // update name as shown in acq settings

      }
   }
   
   @Override
   public boolean isCellEditable(int rowIndex, int colIndex) {
      return colIndex == 1 ? true : false;
   }


}
