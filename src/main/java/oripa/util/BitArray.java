/**
 * ORIPA - Origami Pattern Editor
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * Fixed length bit array.
 *
 * @author OUCHI Koji
 *
 */
public class BitArray {
	private final int[] blocks;

	public final int bitLength;

	private static final int oneBlockBitLength = 32;
	private static final int one = 0x0001;
	private static final int fullBit = 0xFFFF;

	public BitArray(final int bitLength) {
		this.bitLength = bitLength;

		var blockCount = bitLength / oneBlockBitLength + ((bitLength % oneBlockBitLength == 0) ? 0 : 1);
		blocks = new int[blockCount];

		Arrays.fill(blocks, 0);
	}

	public BitArray(final BitArray other) {
		this(other.bitLength);
		setAll(other);
	}

	public void setAll(final BitArray other) {
		if (bitLength != other.bitLength) {
			throw new IllegalArgumentException("bit length should be equal.");
		}

		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = other.blocks[i];
		}
	}

	public boolean get(final int index) {
		if (index >= bitLength) {
			throw new IllegalArgumentException("index should be less than " + bitLength);
		}

		var blockIndex = index / oneBlockBitLength;
		var bitIndex = index % oneBlockBitLength;

		return (blocks[blockIndex] & (one << bitIndex)) != 0;
	}

	public void setOne(final int index) {
		if (index >= bitLength) {
			throw new IllegalArgumentException("index should be less than " + bitLength);
		}

		var blockIndex = index / oneBlockBitLength;
		var bitIndex = index % oneBlockBitLength;

		blocks[blockIndex] |= one << bitIndex;
	}

	public boolean setOneAndGetChange(final int index) {
		if (index >= bitLength) {
			throw new IllegalArgumentException("index should be less than " + bitLength);
		}

		var blockIndex = index / oneBlockBitLength;
		var bitIndex = index % oneBlockBitLength;

		var b = blocks[blockIndex];
		blocks[blockIndex] |= one << bitIndex;

		return b != blocks[blockIndex];
	}

	public void setZero(final int index) {
		if (index >= bitLength) {
			throw new IllegalArgumentException("index should be less than " + bitLength);
		}

		var blockIndex = index / oneBlockBitLength;
		var bitIndex = index % oneBlockBitLength;

		blocks[blockIndex] &= ~(one << bitIndex);
	}

	public boolean setZeroAndGetChange(final int index) {
		if (index >= bitLength) {
			throw new IllegalArgumentException("index should be less than " + bitLength);
		}

		var blockIndex = index / oneBlockBitLength;
		var bitIndex = index % oneBlockBitLength;

		var b = blocks[blockIndex];
		blocks[blockIndex] &= ~(one << bitIndex);

		return b != blocks[blockIndex];
	}

	public BitArray or(final BitArray other) {
		if (bitLength != other.bitLength) {
			throw new IllegalArgumentException("bit length should be equal.");
		}

		var result = new BitArray(bitLength);

		for (int i = 0; i < blocks.length; i++) {
			result.blocks[i] = blocks[i] | other.blocks[i];
		}

		return result;
	}

	public BitArray and(final BitArray other) {
		if (bitLength != other.bitLength) {
			throw new IllegalArgumentException("bit length should be equal.");
		}

		var result = new BitArray(bitLength);

		for (int i = 0; i < blocks.length; i++) {
			result.blocks[i] = blocks[i] & other.blocks[i];
		}

		return result;
	}

	public BitArray not() {
		var result = new BitArray(bitLength);
		for (int i = 0; i < blocks.length; i++) {
			result.blocks[i] = ~blocks[i];
		}

		if (bitLength % oneBlockBitLength != 0) {
			result.blocks[blocks.length - 1] &= fullBit >>> (oneBlockBitLength - bitLength % oneBlockBitLength);
		}
		return result;
	}

	public boolean allZero() {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public void clear() {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = 0;
		}
	}

	public int countOnes() {
		int count = 0;
		for (int i = 0; i < blocks.length; i++) {
			count += Integer.bitCount(blocks[i]);
		}
		return count;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BitArray other) {
			return bitLength == other.bitLength && Arrays.equals(blocks, other.blocks);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bitLength, blocks);
	}

	@Override
	public String toString() {
		return Arrays.stream(blocks)
				.mapToObj(block -> "%8s".formatted(Integer.toHexString(block)).replace(' ', '0'))
				.toList()
				.reversed().stream()
				.reduce("", String::concat);
	}
}
