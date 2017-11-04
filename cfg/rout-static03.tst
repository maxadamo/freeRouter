description static routing with icmp tracker

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
int eth2 eth 0000.0000.1111 $2a$ $2b$
!
vrf def v1
 rd 1:1
 exit
int lo0
 vrf for v1
 ipv4 addr 2.2.2.101 255.255.255.255
 ipv6 addr 4321::101 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.252
 ipv6 addr 1234:1::1 ffff:ffff::
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.2.1 255.255.255.252
 ipv6 addr 1234:2::1 ffff:ffff::
 exit
tracker t1
 vrf v1
 targ 1.1.2.2
 mod icmp
 inter 1000
 time 500
 start
 exit
tracker t2
 vrf v1
 targ 1234:2::2
 mod icmp
 inter 1000
 time 500
 start
 exit
ipv4 route v1 0.0.0.0 0.0.0.0 1.1.1.2 dist 22
ipv6 route v1 :: :: 1234:1::2 dist 22
ipv4 route v1 0.0.0.0 0.0.0.0 1.1.2.2 dist 11 track t1
ipv6 route v1 :: :: 1234:2::2 dist 11 track t2
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
 ipv4 addr 2.2.2.201 255.255.255.255
 ipv6 addr 4321::201 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.252
 ipv6 addr 1234:1::2 ffff:ffff::
 exit
int eth2
 vrf for v1
 ipv4 addr 1.1.2.2 255.255.255.252
 ipv6 addr 1234:2::2 ffff:ffff::
 exit
tracker t1
 vrf v1
 targ 1.1.2.1
 mod icmp
 inter 1000
 time 500
 start
 exit
tracker t2
 vrf v1
 targ 1234:2::1
 mod icmp
 inter 1000
 time 500
 start
 exit
ipv4 route v1 0.0.0.0 0.0.0.0 1.1.1.1 dist 22
ipv6 route v1 :: :: 1234:1::1 dist 22
ipv4 route v1 0.0.0.0 0.0.0.0 1.1.2.1 dist 11 track t1
ipv6 route v1 :: :: 1234:2::1 dist 11 track t2
!


r2 tping 100 3 2.2.2.101 /vrf v1
r2 tping 100 3 4321::101 /vrf v1
r1 tping 100 3 2.2.2.201 /vrf v1
r1 tping 100 3 4321::201 /vrf v1

r2 output show tracker
r2 output show ipv4 route v1
r2 output show ipv6 route v1

r1 send conf t
r1 send int eth2
r1 send shut
r1 send end

r2 tping 100 3 2.2.2.101 /vrf v1
r2 tping 100 3 4321::101 /vrf v1
r1 tping 100 3 2.2.2.201 /vrf v1
r1 tping 100 3 4321::201 /vrf v1

output ../binTmp/rout-track.html
<html><body bgcolor="#000000" text="#FFFFFF" link="#00FFFF" vlink="#00FFFF" alink="#00FFFF">
here is the tracker:
<pre>
<!>show:0
</pre>
here are the ipv4 routes:
<pre>
<!>show:1
</pre>
here are the ipv6 routes:
<pre>
<!>show:2
</pre>
</body></html>
!
