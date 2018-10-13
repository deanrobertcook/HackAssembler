// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

// while (true)
//   for (i=0; i<screenSize; i++)
//     screen[i] = keyboardPressed

  @8192 //(512*256 % 16) go through each address one by one
  D=A
  @max
  M=D

(START)
  @i
  M=0

(LOOP)
  @i
  D=M
  @max
  D=D-M
  @START
  D; JGE    //start whole program again once we've looped through

  @SCREEN
  D=A
  @i
  D=D+M
  @address
  M=D

  @KBD
  D=M
  @NO_KEY
  D; JEQ

  @address
  A=M
  M=-1    //keypressed, set all pixels to on
  @END_LOOP
  0; JEQ //jump over NO_KEY, or else pixels get turned back off

(NO_KEY)
  @address
  A=M
  M=0     // no key pressed, turn off all pixels

(END_LOOP)
  @i
  M=M+1   //increment address counter

  @LOOP   //start loop again
  0; JMP
