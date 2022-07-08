package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 
 * @created: 	2022-07-06
 * @Project:	Reassemble Text Fragments
 */
public class Defragment {

//	private static final boolean isPrint = false;
	
	public static void main(String[] args)  throws IOException {
		
		try (BufferedReader in = new BufferedReader(new FileReader(args[0]))){
				in.lines()
					.map(YourClass::reassemble)
					.forEach(System.out::println);
		}
	}

	static class MatchFragment {

		
		@Override
		public String toString() {
			return "MatchFragment [fragment=" + fragment + ", foreFragment=" + foreFragment + ", foreMaxOverlap="
					+ foreMaxOverlap + ", containFragment="
					+ containFragment + ", postFragment=" + postFragment + ", postMaxOverlap=" + postMaxOverlap + "]";
		}

		private StringBuilder fragment;
		
		private ArrayList<String> foreFragment;
		private int foreMaxOverlap;
		
		private ArrayList<String> containFragment;
		
		private ArrayList<String> postFragment;
		private int postMaxOverlap;
		
		public String getFragment() {
			return this.fragment.toString();
		}
		
		public ArrayList<String> getForeFragment() {
			return foreFragment;
		}

		public ArrayList<String> getContainFragment() {
			return containFragment;
		}
		
		public int getForeMaxOverlap() {
			return foreMaxOverlap;
		}

		public int getPostMaxOverlap() {
			return postMaxOverlap;
		}

		public ArrayList<String> getPostFragment() {
			return postFragment;
		}

		public MatchFragment(String fragment) {
			this.fragment = new StringBuilder();
			this.fragment.append(fragment);
			
			this.foreFragment = new ArrayList<>();
			this.containFragment = new ArrayList<>();
			this.postFragment = new ArrayList<>();

			this.foreMaxOverlap = 0;
			this.postMaxOverlap = 0;
		}
		
		public void appendFragment(String otherFragment, boolean fore) {
			if (fore) {
				int overLapChar = YourClass.matchStart(otherFragment, this.fragment.toString());
				YourClass.append(YourClass.POS.start, overLapChar, otherFragment, fragment);
			}else {
				int overLapChar = YourClass.matchEnd(otherFragment, this.fragment.toString());
				YourClass.append(YourClass.POS.end, overLapChar, otherFragment, fragment);
			}
		}
		
		public void clearCompare() {
			this.foreFragment.clear();
			this.postFragment.clear();
			this.containFragment.clear();

			this.foreMaxOverlap = 0;
			this.postMaxOverlap = 0;
		}
		
		public void compareFragment(String otherFragment) {
			if (this.fragment.toString().equals(otherFragment)) {
				return;
			}
			
			int foreOverlap = YourClass.matchStart(otherFragment, this.fragment.toString());
			if (foreOverlap > 0) {
				this.foreFragment.add(otherFragment);
				if (this.foreMaxOverlap < foreOverlap) {
					this.foreMaxOverlap = foreOverlap;
				}
			}
			
			int postOverlap = YourClass.matchEnd(otherFragment, this.fragment.toString());
			if (postOverlap > 0) {
				this.postFragment.add(otherFragment);
				if (this.postMaxOverlap < postOverlap) {
					this.postMaxOverlap = postOverlap;
				}
			}
			if (YourClass.matchContain(otherFragment, this.fragment.toString()) > -1) {
				this.containFragment.add(otherFragment);
			}
		}
		
		public void removeLinkFragment(List<String> removeFragments, YourClass.POS pos) {
			if (YourClass.POS.start==pos) this.foreFragment.removeAll(removeFragments);
			if (YourClass.POS.end==pos) this.postFragment.removeAll(removeFragments);
			this.containFragment.removeAll(removeFragments);
		}
		
		public boolean isUniqueLink() {
			return (this.foreFragment.size() + this.postFragment.size() == 1);
		}
	}

	/**
	 * Class contains function to reassemble the input fragments.
	 *
	 */
	static class YourClass{
		
		/**
		 * Logic:
		 * 	1)	Handle the unique mapping fragment (1:1 mapping only first)
		 *  2)	Compare all fragments and process the highest overlapping fragment pair first.
		 * @param s "String contains fragments with semi-colon as delimiter
		 * @return	"Return string with the defragmented string.
		 */
		public static String reassemble(String s) {
			
			StringBuilder sb = new StringBuilder();
			
			/**
			 * Sort the fragments with the longer length first.
			 */
			ArrayList<String> fragments = (ArrayList<String>) Arrays.asList(s.split(";")).stream()
					.sorted((s1, s2)-> s2.length()-s1.length())
					.collect(Collectors.toList());
			
//			fragments.forEach(System.out::println);
			
			ArrayList<MatchFragment> fragList = new ArrayList<>();
			for (String f: fragments) {
				fragList.add(new MatchFragment(f));
			}
			
			fragList.forEach((mf)->{
				for(String f: fragments) {
					mf.compareFragment(f);
				}
			});
			
//			if (isPrint)	fragList.forEach(System.out::println);
			
			/**
			 * Unique mapping first
			 * Find the unique mapping and process it first.
			 * The merged fragment will be removed from list.
			 */
			while(fragList.stream().anyMatch(mf->mf.isUniqueLink())) {
				List<MatchFragment> uniqueMatchFragment = fragList.stream().filter(mf -> mf.isUniqueLink()).collect(Collectors.toList());
				ArrayList<String> removeForeFragments = new ArrayList<>();
				ArrayList<String> removePostFragments = new ArrayList<>();
				
				for(MatchFragment unique: uniqueMatchFragment) {
					if (unique.getForeFragment().size()==1) {
						String appendFragment = unique.getForeFragment().get(0);
						unique.appendFragment(appendFragment, true);
						removeForeFragments.add(appendFragment);
					}else if (unique.getPostFragment().size()==1){
						String appendFragment = unique.getPostFragment().get(0);
						unique.appendFragment(appendFragment, false);
						removePostFragments.add(appendFragment);
					}
				}
				
				fragments.removeAll(removeForeFragments);
				fragments.removeAll(removePostFragments);
				fragList.forEach( mf -> {
					mf.removeLinkFragment(removeForeFragments, POS.start);
					mf.removeLinkFragment(removePostFragments, POS.end);
				});

				for(String rm: removeForeFragments) {
					fragList.removeIf(mf -> mf.getFragment().equals(rm));
//					if (isPrint)	System.out.println("Remove Link String: " + rm);
				}
				for(String rm: removePostFragments) {
					fragList.removeIf(mf -> mf.getFragment().equals(rm));
//					if (isPrint)	System.out.println("Remove Link String: " + rm);
				}
			
				fragList = (ArrayList<MatchFragment>) fragList.stream().filter(distinctByKey(MatchFragment::getFragment)).collect(Collectors.toList());
				
				fragments.clear();
				fragments.addAll(fragList.stream().map(MatchFragment::getFragment).collect(Collectors.toList()));
				
				fragList.forEach((mf)->{
					mf.clearCompare();
					for(String f: fragments) {
						mf.compareFragment(f);
					}
					fragments.removeAll(mf.getContainFragment());
				});
			}
			
			/**
			 * If no more fragment found, mapping completed.
			 */
			if (fragList.size() == 1) {
//				if (isPrint)	System.out.println("\n\nResult Found: ");
				return fragList.get(0).getFragment();
			}

			/**
			 * If there are still more than one fragment, 
			 * try to map those fragment with the logic as:
			 * 1)	Map with rule the more overlap first.
			 */
			fragments.clear();
			
			fragments.addAll(fragList.stream().filter(distinctByKey(MatchFragment::getFragment)).sorted((mp1, mp2) 
					-> Math.max(mp2.getForeMaxOverlap(), mp2.getPostMaxOverlap()) - Math.max(mp1.getForeMaxOverlap(), mp1.getPostMaxOverlap()))
					.map(MatchFragment::getFragment)
					.collect(Collectors.toList()));
			
			fragList.clear();
			
			sb.append(fragments.get(0));
			fragments.remove(0);
			
			while (!fragments.isEmpty()) {
				int maxOverLapSize = 0;
				String maxFragment = null;
				POS pos = POS.none;
				
				for(String f: fragments) {
					int matchStartChar = matchStart(f, sb.toString());
					int matchEndChar = matchEnd(f, sb.toString());
					int containsChar = matchContain(f, sb.toString());
					
					if (containsChar > -1) {
						maxOverLapSize = f.length();
						maxFragment = f;
						pos = POS.contains;
						break;
					}else if (matchStartChar > maxOverLapSize && matchStartChar > matchEndChar && matchStartChar > 0) {
						/**Assume it should append to the start of Defragment String */
						maxOverLapSize = matchStartChar;
						maxFragment = f;
						pos = POS.start;
					}else if (matchEndChar > maxOverLapSize && matchEndChar > 0) {
						/** Assume it should append to the end of Defragment String */
						maxOverLapSize = matchEndChar;
						maxFragment = f;
						pos = POS.end;
					}
				}

				if (null != maxFragment) {
					append(pos, maxOverLapSize, maxFragment, sb);
					fragments.remove(maxFragment);
				}
			}
			
//			if (!fragments.isEmpty() && isPrint) {
//				System.out.println("__________________________________");
//				fragments.forEach(System.out::println);
//			}
			return sb.toString();
		}
		
		public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		    Set<Object> seen = ConcurrentHashMap.newKeySet();
		    return t -> seen.add(keyExtractor.apply(t));
		}
		
		enum POS{
			start, end, contains, none
		}

		public static void append(POS pos, int overlapSize, String fragment, StringBuilder defragment) {
			if (POS.start == pos) {
//				if (isPrint) {
//					System.out.println(defragment.toString());
//					System.out.println(">>" + fragment);
//					System.out.println("[Start Overlap]: " + fragment.substring(fragment.length()-overlapSize+1));
//				}
				defragment.insert(0, fragment.substring(0, fragment.length()-overlapSize));
			}else if (POS.end == pos) {
//				if (isPrint) {
//					System.out.println(defragment.toString());
//					System.out.println(">>" + fragment);
//					System.out.println("[End Overlap]: " + fragment.substring(0, overlapSize-1));
//				}
				defragment.append(fragment.substring(overlapSize));
			}
		}
		
		/**
		 * Using the shorter String to compare the longer String to minimize the looping size.
		 * 
		 * @param shortString
		 * @param longString
		 * @return
		 */
		public static int matchStart(String shortString, String longString) {
			int shortStringEndIndex = shortString.length()-1;
			for(int i=0; i<=shortStringEndIndex;i++) {
				if (longString.startsWith(shortString.substring(i))) {
					return shortString.length() -i;
				};
			}
			return 0;
		}
		
		/**
		 * Using the shorter String to compare the longer String to minimize the looping size.
		 * 
		 * @param shortString
		 * @param longString
		 * @return
		 */
		public static int matchEnd(String shortString, String longString) {
			int shortStringEndIndex = shortString.length()-1;
			for(int i=shortStringEndIndex; i>=1;i--) {
				if (longString.endsWith(shortString.substring(0, i))) {
//					System.out.println("\n" + longString + "\n[End ("+(i)+")]" + shortString + "["+shortString.substring(0, i)+"]");
					return i;
				};
			}
			return 0;
		}
		
		public static int matchContain(String shortString, String longString) {
			return longString.indexOf(shortString);
		}
	}
}
