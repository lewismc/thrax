package edu.jhu.thrax.datatypes;

import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.Assert;

public class ArrayAlignmentTest
{
	@Test
	public void sourceIndexIsAligned_IndexNotPresent_returnsFalse()
	{
		ArrayAlignment a = ArrayAlignment.fromString("0-0 2-2", false);
		Assert.assertFalse(a.sourceIndexIsAligned(1));
	}

	@Test
	public void targetIndexIsAligned_IndexNotPresent_returnsFalse() 
	{ 
		ArrayAlignment a = ArrayAlignment.fromString("0-0 2-2", false); 
		Assert.assertFalse(a.targetIndexIsAligned(1));
	}

	@Test
	public void sourceIndexIsAligned_IndexOutOfRange_returnsFalse()
	{
		ArrayAlignment a = ArrayAlignment.fromString("0-0 2-2", false); 
		Assert.assertFalse(a.sourceIndexIsAligned(-1));
		Assert.assertFalse(a.sourceIndexIsAligned(3));
	}

	@Test
	public void targetIndexIsAligned_IndexOutOfRange_returnsFalse()
	{
		ArrayAlignment a = ArrayAlignment.fromString("0-0 2-2", false); 
		Assert.assertFalse(a.targetIndexIsAligned(-1));
		Assert.assertFalse(a.targetIndexIsAligned(3));
	}

	@Test
	public void sourceIndexIsAligned_IndexPresent_returnsTrue()
	{
		ArrayAlignment a = ArrayAlignment.fromString("0-0 1-2 77-32", false);
		Assert.assertTrue(a.sourceIndexIsAligned(0));
		Assert.assertTrue(a.sourceIndexIsAligned(1));
		Assert.assertTrue(a.sourceIndexIsAligned(77));
	}

	@Test
	public void targetIndexIsAligned_IndexPresent_returnsTrue()
	{
		ArrayAlignment a = ArrayAlignment.fromString("0-0 1-2 77-32", false);
		Assert.assertTrue(a.targetIndexIsAligned(0));
		Assert.assertTrue(a.targetIndexIsAligned(2));
		Assert.assertTrue(a.targetIndexIsAligned(32));
	}
}

