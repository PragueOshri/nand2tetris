// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// PseudoCode
//     i = 0
//     R2 = 0
//     if (R0 == 0) goto END
//     LOOP:
//         if (i == R1) goto END
//         R2 = R2 + R0
//         i = i + 1
//         goto LOOP
//     END:

// i, R2 = 0
    @i
    M=0
    @R2
    M=0
// if (R0 == 0) goto END
    @R0
    D=M
    @END
    D;JEQ
(LOOP)
    // if (i == R1) goto END
    @i
    D=M
    @R1
    D=D-M
    @END
    D;JEQ
    // R2 = R2 + R0
    @R0
    D=M
    @R2
    D=M+D
    M=D
    // i = i + 1
    @i
    M=M+1
    // goto LOOP
    @LOOP
    0;JMP
(END)
    @END
    0;JMP
