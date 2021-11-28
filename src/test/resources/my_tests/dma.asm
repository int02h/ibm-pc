use16

start:

cli
mov dx, 00h ; channel
mov cx, 8

test:

; put the channel number in first memory byte
mov byte[0], dl

; check that 16 bit register value can written and read in two parts

; reset controller to clear registers
mov al, 1
out 0Dh, al

mov al, 34h ; write lsb
out dx, al
mov al, 12h ; write msb
out dx, al

in al, dx ; read lsb
mov bl, al
in al, dx ; read msb
mov bh, al

cmp bx, 1234h
jnz finish

; then check that flip flop can be reset in the middle of writing

; reset controller to clear registers
mov al, 1
out 0Dh, al

mov al, 0AAh ; write lsb
out dx, al

mov al, 0EEh
out 0Ch, al  ; write any value to reset flip flop

mov al, 0BBh ; write lsb again
out dx, al

mov al, 0EEh
out 0Ch, al  ; write any value to reset flip flop

in al, dx ; read lsb
mov bl, al
in al, dx ; read msb
mov bh, al

cmp bx, 00BBh
jnz finish

; and that flip flop can be reset in the middle of reading

; reset controller to clear registers
mov al, 1
out 0Dh, al

mov al, 0CCh ; write lsb
out dx, al
mov al, 0DDh ; write msb
out dx, al

in al, dx ; read lsb
mov bl, al

mov al, 0ABh
out 0Ch, al  ; write any value to reset flip flop

in al, dx ; read lsb again
mov bh, al

cmp bx, 0CCCCh
jnz finish

inc dx ; proceed to the next channel

loop test

; check flip flop flag is shared between registers

; reset controller to clear registers
mov al, 1
out 0Dh, al

mov al, 11h
out 00h, al ; lsb to address register of channel 0

mov al, 0AAh
out 07h, al ; msb to count register of channel 3

in al, 00h ; read lsb of address register of channel 0
mov bl, al
in al, 00h ; read msb of address register of channel 0
mov bh, al

cmp bx, 0011h
jnz finish

in al, 07h ; read lsb of count register of channel 3
mov bl, al
in al, 07h ; read msb of count register of channel 3
mov bh, al

cmp bx, 0AA00h
jnz finish

mov byte[0], 0FFh ; success

finish:
hlt

times 65520 - ($ - $$) db 0
jmp start
times 65536 - ($ - $$) db 0