package fragment.submissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fragment.submissions.Defragment.YourClass;

class Test_Defragment {
	
	private static final String srcFolder = "{your\\absolute\\file\\path}";

	@Test
	@Disabled
	@DisplayName("Test Given Fragments Text File")
	void testFragments() {
		String filePath = srcFolder + "reassemble-text-fragments-example.txt";
		String[] args = new String[] {filePath};
		try {
			Defragment.main(args);
		} catch (IOException e) {
			fail("Exception should not occurred.");
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Test Case 1 from Example")
	void TestCase1() {
		String input = "O draconia;conian devil! Oh la;h lame sa;saint!";
		String expected = "O draconian devil! Oh lame saint!";
		
		String actualResult = YourClass.reassemble(input);
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 2 from Example")
	void TestCase2() {
		String input = "m quaerat voluptatem.;pora incidunt ut labore et d;, consectetur, adipisci velit;olore magnam aliqua;idunt ut labore et dolore magn;uptatem.;i dolorem ipsum qu;iquam quaerat vol;psum quia dolor sit amet, consectetur, a;ia dolor sit amet, conse;squam est, qui do;Neque porro quisquam est, qu;aerat voluptatem.;m eius modi tem;Neque porro qui;, sed quia non numquam ei;lorem ipsum quia dolor sit amet;ctetur, adipisci velit, sed quia non numq;unt ut labore et dolore magnam aliquam qu;dipisci velit, sed quia non numqua;us modi tempora incid;Neque porro quisquam est, qui dolorem i;uam eius modi tem;pora inc;am al";
		String expected = "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.";
		
		String actualResult = YourClass.reassemble(input);
		
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 3")
	void TestCase3() {
		String input = "01234;34;789abcdefghij;012345;jklmn;89abcd;hijklmn;456789ab;fghijklmn";
		String expected = "0123456789abcdefghijklmn";
		
		String actualResult = YourClass.reassemble(input);
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 4")
	void TestCase4() {
		String input = "are duplicate text are.;are dup;are duplicate text are.;If there are;are duplicate text, for example, there are;are duplicate text, for";
		String expected = "If there are duplicate text, for example, there are duplicate text are.";
		
		String actualResult = YourClass.reassemble(input);
		
		System.out.println(expected);
		System.out.println(actualResult);
		
		assertEquals(expected, actualResult);
	}
	
	
	@Test
	@DisplayName("Test Case 5")
	void TestCase5() {
		String input = "now repeat; repeat!;repeat, now";
		String expected = "repeat, now repeat!";
		
		String actualResult = YourClass.reassemble(input);
		
		System.out.println(expected);
		System.out.println(actualResult);
		
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 6")
	void TestCase6() {
		String input = "abcdef;abcdef;fghab;habk";
		String expected = "abcdefghabk";
		
		String actualResult = YourClass.reassemble(input);
		
		System.out.println(expected);
		System.out.println(actualResult);
		
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 7")
	void TestCase7() {
		String input = "abcdefgh;hih;hhi";
		String expected = "abcdefghhih";
		
		String actualResult = YourClass.reassemble(input);
		
		System.out.println(expected);
		System.out.println(actualResult);
		
		assertEquals(expected, actualResult);
	}
	
	@Test
	@DisplayName("Test Case 8")
	void TestCase8() {
		String input = "jkabcdefh;xxefhi;efhijk";
		String expected = "xxefhijkabcdefh";
		
		String actualResult = YourClass.reassemble(input);
		
		System.out.println(expected);
		System.out.println(actualResult);
		
		assertEquals(expected, actualResult);
	}
	
	
	
}
