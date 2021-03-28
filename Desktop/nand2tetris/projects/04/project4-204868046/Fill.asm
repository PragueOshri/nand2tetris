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

// PseudoCode
// while(true)
//  if(RAM[KBD] == 0)
//      for (i=SCREEN, i<=SCREEN*256, i=i+1)
//          RAM[i] = 0
//  else
//      for (i=SCREEN, i<=SCREEN*256, i=i+1)
//          RAM[i] = -1

// Initialize
    @i
    M=0
(LOOP)
    // if(RAM[KBD] == 0) goto WHITE
    @KBD
    D=M
    @WHITE
    D;JEQ

(BLACK)
    @SCREEN
    D=A
    @arr
    M=D // starting an array from SCREEN(=16384)
    @8192
    D=A
    @n
    M=D // end of the array after 8192 rows
    @i
    M=0

    (BLACKLOOP)
        @i
        D=M
        @n
        D=D-M
        // if i == n (colored all the rows) goto LOOP
        @LOOP
        D;JEQ

        @arr
        D=M
        @i
        A=D+M
        M=-1
        // i = i + 1
        @i
        M=M+1
        // goto BLACKLOOP
        @BLACKLOOP
        0;JMP

(WHITE)
    @SCREEN
    D=A
    @arr
    M=D // starting an array from SCREEN(=16384)
    @8192
    D=A
    @n
    M=D // end of the array after 8192 rows
    @i
    M=0

    (WHITELOOP)
        @i
        D=M
        @n
        D=D-M
        // if i == n (colored all the rows) goto LOOP
        @LOOP
        D;JEQ

        @arr
        D=M
        @i
        A=D+M
        M=0
        // i = i + 1
        @i
        M=M+1
        // goto WHITELOOP
        @WHITELOOP
        0;JMP
