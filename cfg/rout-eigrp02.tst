description eigrp point2multipoint connection

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
bridge 1
 exit
router eigrp4 1
 vrf v1
 router 4.4.4.1
 as 1
 red conn
 exit
router eigrp6 1
 vrf v1
 router 6.6.6.1
 as 1
 red conn
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 bridge-gr 1
 exit
int bvi1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 router eigrp4 1 ena
 router eigrp6 1 ena
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
bridge 1
 mac-learn
 exit
router eigrp4 1
 vrf v1
 router 4.4.4.2
 as 1
 red conn
 exit
router eigrp6 1
 vrf v1
 router 6.6.6.2
 as 1
 red conn
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 bridge-gr 1
 exit
int eth2
 bridge-gr 1
 exit
int bvi1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234::2 ffff::
 router eigrp4 1 ena
 router eigrp6 1 ena
 exit
!

addrouter r3
int eth1 eth 0000.0000.3333 $2b$ $2a$
int eth2 eth 0000.0000.3333 $3a$ $3b$
!
vrf def v1
 rd 1:1
 exit
bridge 1
 mac-learn
 exit
router eigrp4 1
 vrf v1
 router 4.4.4.3
 as 1
 red conn
 exit
router eigrp6 1
 vrf v1
 router 6.6.6.3
 as 1
 red conn
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.3 255.255.255.255
 ipv6 addr 4321::3 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 bridge-gr 1
 exit
int eth2
 bridge-gr 1
 exit
int bvi1
 vrf for v1
 ipv4 addr 1.1.1.3 255.255.255.0
 ipv6 addr 1234::3 ffff::
 router eigrp4 1 ena
 router eigrp6 1 ena
 exit
!

addrouter r4
int eth1 eth 0000.0000.4444 $3b$ $3a$
!
vrf def v1
 rd 1:1
 exit
bridge 1
 exit
router eigrp4 1
 vrf v1
 router 4.4.4.4
 as 1
 red conn
 exit
router eigrp6 1
 vrf v1
 router 6.6.6.4
 as 1
 red conn
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.4 255.255.255.255
 ipv6 addr 4321::4 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 bridge-gr 1
 exit
int bvi1
 vrf for v1
 ipv4 addr 1.1.1.4 255.255.255.0
 ipv6 addr 1234::4 ffff::
 router eigrp4 1 ena
 router eigrp6 1 ena
 exit
!


r1 tping 100 40 2.2.2.2 /vrf v1
r1 tping 100 40 2.2.2.3 /vrf v1
r1 tping 100 40 2.2.2.4 /vrf v1
r1 tping 100 40 4321::2 /vrf v1
r1 tping 100 40 4321::3 /vrf v1
r1 tping 100 40 4321::4 /vrf v1

r2 tping 100 40 2.2.2.1 /vrf v1
r2 tping 100 40 2.2.2.3 /vrf v1
r2 tping 100 40 2.2.2.4 /vrf v1
r2 tping 100 40 4321::1 /vrf v1
r2 tping 100 40 4321::3 /vrf v1
r2 tping 100 40 4321::4 /vrf v1

r3 tping 100 40 2.2.2.1 /vrf v1
r3 tping 100 40 2.2.2.2 /vrf v1
r3 tping 100 40 2.2.2.4 /vrf v1
r3 tping 100 40 4321::1 /vrf v1
r3 tping 100 40 4321::2 /vrf v1
r3 tping 100 40 4321::4 /vrf v1

r4 tping 100 40 2.2.2.1 /vrf v1
r4 tping 100 40 2.2.2.2 /vrf v1
r4 tping 100 40 2.2.2.3 /vrf v1
r4 tping 100 40 4321::1 /vrf v1
r4 tping 100 40 4321::2 /vrf v1
r4 tping 100 40 4321::3 /vrf v1
