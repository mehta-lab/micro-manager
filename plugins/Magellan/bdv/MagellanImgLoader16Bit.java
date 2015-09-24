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

package bdv;

import acq.MultiResMultipageTiffStorage;
import bdv.img.cache.CachedCellImg;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileShortArray;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.volatiles.VolatileUnsignedShortType;

public class MagellanImgLoader16Bit extends MagellanImgLoader< UnsignedShortType, VolatileUnsignedShortType, VolatileShortArray> {

   public MagellanImgLoader16Bit(MultiResMultipageTiffStorage storage) {
      super(storage, new MultiResMPTiffVolatileShortArrayLoader(storage), new UnsignedShortType(), new VolatileUnsignedShortType());
   }

   @Override
   protected void linkType(final CachedCellImg< UnsignedShortType, VolatileShortArray> img) {
      img.setLinkedType(new UnsignedShortType(img));
   }

   @Override
   protected void linkVolatileType(final CachedCellImg< VolatileUnsignedShortType, VolatileShortArray> img) {
      img.setLinkedType(new VolatileUnsignedShortType(img));
   }
}
