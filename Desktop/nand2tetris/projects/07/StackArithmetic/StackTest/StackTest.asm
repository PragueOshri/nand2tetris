@17
D=A
@SP
A=M
M=D
@SP
M=M+1
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isEQ0
D;JEQ
@SP
A=M-1
M=0
@notEQ0
0;JMP
(isEQ0)
@SP
A=M-1
M=-1
(notEQ0)
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isEQ1
D;JEQ
@SP
A=M-1
M=0
@notEQ1
0;JMP
(isEQ1)
@SP
A=M-1
M=-1
(notEQ1)
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isEQ2
D;JEQ
@SP
A=M-1
M=0
@notEQ2
0;JMP
(isEQ2)
@SP
A=M-1
M=-1
(notEQ2)
@892
D=A
@SP
A=M
M=D
@SP
M=M+1
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isLT3
D;JLT
@SP
A=M-1
M=0
@notLT3
0;JMP
(isLT3)
@SP
A=M-1
M=-1
(notLT3)
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
@892
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isLT4
D;JLT
@SP
A=M-1
M=0
@notLT4
0;JMP
(isLT4)
@SP
A=M-1
M=-1
(notLT4)
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isLT5
D;JLT
@SP
A=M-1
M=0
@notLT5
0;JMP
(isLT5)
@SP
A=M-1
M=-1
(notLT5)
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isGT6
D;JGT
@SP
A=M-1
M=0
@notGT6
0;JMP
(isGT6)
@SP
A=M-1
M=-1
(notGT6)
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isGT7
D;JGT
@SP
A=M-1
M=0
@notGT7
0;JMP
(isGT7)
@SP
A=M-1
M=-1
(notGT7)
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
D=M-D
@isGT8
D;JGT
@SP
A=M-1
M=0
@notGT8
0;JMP
(isGT8)
@SP
A=M-1
M=-1
(notGT8)
@57
D=A
@SP
A=M
M=D
@SP
M=M+1
@31
D=A
@SP
A=M
M=D
@SP
M=M+1
@53
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
M=M+D
@112
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
M=M-D
@SP
A=M-1
M=-M
@SP
AM=M-1
D=M
A=A-1
M=M&D
@82
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
AM=M-1
D=M
A=A-1
M=M|D
@SP
A=M-1
M=!M
