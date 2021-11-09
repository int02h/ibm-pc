use16

start:
mov ax, 1h
mov ds, ax

mov ax, 2h
mov ss, ax

mov bx, 1
mov bp, 2
mov si, 3
mov di, 5

mov byte[bx + si], 1
mov byte[bx + di], 2
mov byte[bp + si], 3
mov byte[bp + di], 4
mov byte[si], 5
mov byte[di], 6
mov byte[10h], 7
mov byte[bx], 8

hlt

times 65520 - ($ - $$) db 0
jmp start
times 65536 - ($ - $$) db 0