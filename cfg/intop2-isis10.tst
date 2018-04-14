description interop2: isis sr

addrouter r1
int eth1 eth 0000.0000.1111 $rem1$
int eth2 eth 0000.0000.1112 $rem2$
!
vrf def v1
 rd 1:1
 exit
access-list test4
 sequence 10 deny 1 any all any all
 sequence 20 permit all any all any all
 exit
access-list test6
 sequence 10 deny all 4321:: ffff:: all 4321:: ffff:: all
 sequence 20 permit all any all any all
 exit
router isis4 1
 vrf v1
 net 48.4444.0000.1111.00
 traffeng 2.2.2.1
 segrout 10
 is-type level2
 both segrout
 red conn
 exit
router isis6 1
 vrf v1
 net 48.6666.0000.1111.00
 traffeng 6.6.6.1
 segrout 10
 is-type level2
 multi-topology
 both segrout
 red conn
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 router isis4 1 ena
 mpls enable
 ipv4 access-group-in test4
! ipv4 access-group-out test4
 exit
int eth2
 vrf for v1
 ipv6 addr fe80::1 ffff::
 router isis6 1 ena
 mpls enable
 ipv6 access-group-in test6
! ipv6 access-group-out test6
 exit
int lo1
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 router isis4 1 ena
 router isis4 1 segrout index 1
 router isis4 1 segrout node
 exit
int lo2
 vrf for v1
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 router isis6 1 ena
 router isis6 1 segrout index 2
 router isis6 1 segrout node
 exit
!

addremote r2
int eth1 eth 0000.0000.2222 $rem1$
int eth2 eth 0000.0000.2223 $rem2$
!
interface loopback0
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2/128
 exit
interface gigabit0/0/0/0
 ipv4 address 1.1.1.2 255.255.255.0
 no shutdown
 exit
interface gigabit0/0/0/1
 ipv6 enable
 no shutdown
 exit
router isis 1
 net 48.0000.0000.1234.00
 address-family ipv4 unicast
  metric-style wide
  mpls traffic-eng level-2-only
  mpls traffic-eng router-id Loopback0
  segment-routing mpls sr-prefer
  redistribute connected
 address-family ipv6 unicast
  metric-style wide
  segment-routing mpls sr-prefer
  redistribute connected
 interface gigabit0/0/0/0
  point-to-point
  hello-padding disable
  address-family ipv4 unicast
 interface gigabit0/0/0/1
  point-to-point
  hello-padding disable
  address-family ipv6 unicast
 interface Loopback0
  passive
  address-family ipv4 unicast prefix-sid index 3
  address-family ipv6 unicast prefix-sid index 4
root
commit
!


r1 tping 0 10 1.1.1.2 /vrf v1
r1 tping 100 60 2.2.2.2 /vrf v1 /int lo1
r1 tping 100 60 4321::2 /vrf v1 /int lo2
