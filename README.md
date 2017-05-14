# Bit Packing
  Simple java based bit packing utility class which packs values into contiguous byte array.
  It also exposes access by position and update to packed array by position. 
  ```java
  void pack(int[] values, int valueStart, int length, byte[] packedBytes, int byteStart, int bitsPerValue);
  void unPack(int[] values, int valueStart, int length, byte[] packedBytes, int byteStart, int bitsPerValue);
  int get(byte[] packedBytes, int at, int bitsPerValue);
  void set(byte[] packedBytes, int at, int value, int bitsPerValue);
  int calculatePackedSize(int size, int bitsPerValue);
  int getBitsPerValue(int[] values);
  ```
  
  
