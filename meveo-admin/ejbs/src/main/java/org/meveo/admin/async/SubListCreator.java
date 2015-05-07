/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;



/**
 * @author anasseh
 *
 */


public class SubListCreator {

	private int nbThreads=1;
	private List<?> theBigList;
	private boolean hasNext=true;
	private int from;
	private int to;
	private int blocToRun ;
	private int modulo ;
	private int listSize;

	public SubListCreator(List<?> theList, int nbRuns) throws Exception{
		if(nbRuns < 1){
			throw new Exception("nbRuns should not be < 1 ");
		}
		if(theList == null || theList.isEmpty()){
			throw new Exception("The list should not be empty");
		}
		this.theBigList = theList;
		this.nbThreads = nbRuns;
		
		listSize = theBigList.size();
		if(nbThreads > listSize) {
			nbThreads = listSize;
		}
		blocToRun = listSize/nbThreads;
		modulo = listSize % nbThreads;
		from=0;
		to=blocToRun;
	}

	public List<?> getNextWorkSet(){
		List<?> toRuns = theBigList.subList(from,to );
		from = to;
		to = from + blocToRun;
		if(listSize - modulo == to){
			to+=modulo;
		}
		if(from==listSize) {
			hasNext=false;
		}
		return toRuns;
	}

	
	/**
	 * @return the hasNext
	 */
	public boolean isHasNext() {
		return hasNext;
	}

	
}
