/**
  * This file is part of OsciPrime
  *
  * Copyright (C) 2011 - Manuel Di Cerbo, Andreas Rudolf
  * 
  * Nexus-Computing GmbH, Switzerland 2011
  *
  * OsciPrime is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.
  *
  * OsciPrime is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with OsciPrime; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin St, Fifth Floor, 
  * Boston, MA  02110-1301  USA
  */
package ch.serverbox.android.osciprime.hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

/**
 * @deprecated
 * @author mdc
 *
 */
public class FxLoader {
	//fxload -t fx2 -I fifo.hex  -D /dev/bus/usb/001/005
	public static int loadHex(String hex){ 
		Runtime rt = Runtime.getRuntime();
		boolean found = false;
		try {
			Process p = rt.exec("lsusb");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line ="";
			int devNum = 6; // wild guess
			while((line = br.readLine()) != null){
				l(line);
				if(line.contains("04b4:8613") || line.contains("04b4:1004")){
					if(line.contains("04b4:1004")){
						br.close();
						l("found fx2, returning");
						return 0; 
					}
					int loc = line.indexOf("device ")+7;
					int stop = line.indexOf(")");
					devNum = Integer.valueOf(line.substring(loc, stop));
					found = true;
					break;
				}  
			}
			l("closing");
			br.close();
			l("closed");
			if(!found){//did not find the device
				l("device not found");
				return -1;
			}else{
				l("device found");
			}
			Process prFX = rt.exec("fxload -t fx2 -I "+hex+" -D /dev/bus/usb/001/"+String.format("%03d", devNum));
			prFX.waitFor();
			Thread.sleep(2000); //give the fx2 some time to register
		}
		catch (IOException e1){
			e(e1);
		}
		catch (InterruptedException e){
			e(e);
		}
		return 0;
	}
	
	   private  static void l(Object s){
	    	Log.d("FXLOAD", ">==< "+s.toString()+" >==<");
	    }
	    
	    private static void e(Object s){
	    	Log.e("FXLOAD", ">==< "+s.toString()+" >==<");
	    }
}
