description ebgp with nexthop tracking routepolicy

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
int eth2 eth 0000.0000.1111 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.1 255.255.255.255
 ipv6 addr 4321::1 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.1.5 255.255.255.252
 ipv6 addr 1234:2::1 ffff:ffff::
 exit
route-policy rm1
 if distance 0
  pass
 enif
 exit
route-policy rm2
 set aspath 3 3 3
 pass
 exit
router bgp4 1
 vrf v1
 address uni
 local-as 1
 router-id 4.4.4.1
 nexthop route-policy rm1
 neigh 1.1.1.2 remote-as 2
 neigh 1.1.1.2 route-policy-in rm2
 neigh 1.1.1.2 route-policy-out rm2
 neigh 1.1.1.6 remote-as 2
 red conn
 exit
router bgp6 1
 vrf v1
 address uni
 local-as 1
 router-id 6.6.6.1
 nexthop route-policy rm1
 neigh 1234:1::2 remote-as 2
 neigh 1234:1::2 route-policy-in rm2
 neigh 1234:1::2 route-policy-out rm2
 neigh 1234:2::2 remote-as 2
 red conn
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
int eth2 eth 0000.0000.2222 $2b$ $2a$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.2 255.255.255.255
 ipv6 addr 4321::2 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234:1::2 ffff:ffff::
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.1.6 255.255.255.252
 ipv6 addr 1234:2::2 ffff:ffff::
 exit
route-policy rm1
 if distance 0
  pass
 enif
 exit
router bgp4 1
 vrf v1
 address uni
 local-as 2
 router-id 4.4.4.2
 nexthop route-policy rm1
 neigh 1.1.1.1 remote-as 1
 neigh 1.1.1.5 remote-as 1
 red conn
 exit
router bgp6 1
 vrf v1
 address uni
 local-as 2
 router-id 6.6.6.2
 nexthop route-policy rm1
 neigh 1234:1::1 remote-as 1
 neigh 1234:2::1 remote-as 1
 red conn
 exit
!


r1 tping 100 60 2.2.2.2 /vrf v1
r1 tping 100 60 4321::2 /vrf v1
r2 tping 100 60 2.2.2.1 /vrf v1
r2 tping 100 60 4321::1 /vrf v1

r1 tping 100 3 2.2.2.2 /vrf v1 /int lo0
r1 tping 100 3 4321::2 /vrf v1 /int lo0
r2 tping 100 3 2.2.2.1 /vrf v1 /int lo0
r2 tping 100 3 4321::1 /vrf v1 /int lo0

r1 send conf t
r1 send int eth2
r1 send shut
r1 send end

r2 send conf t
r2 send int eth2
r2 send shut
r2 send end

r1 tping 100 3 2.2.2.2 /vrf v1 /int lo0
r1 tping 100 3 4321::2 /vrf v1 /int lo0
r2 tping 100 3 2.2.2.1 /vrf v1 /int lo0
r2 tping 100 3 4321::1 /vrf v1 /int lo0
