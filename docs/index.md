# Android extensions for Apache HttpClient

## History of Apache HttpClient on Android

Google Android 1.0 was released with a pre-BETA snapshot of Apache HttpClient. To coincide with
the first Android release Apache HttpClient 4.0 APIs had to be frozen prematurely, while many of
interfaces and internal structures were still not fully worked out. 

As Apache HttpClient 4.0 was maturing the project was expecting Google to incorporate the latest 
code improvements into their code tree. Unfortunately it did not happen. Version of Apache 
HttpClient shipped with Android has effectively become a fork. 

Eventually Google decided to discontinue further development of their fork while refusing 
to upgrade to the stock version of Apache HttpClient citing compatibility concerns as a reason 
for such decision. Google completely removed their fork of Apache HttpClient from Android in 
version 8.0 (API 26) only.

## Project scope and objectives

The main objective of this project is to ensure that official Apache HttpClient releases can 
be used on Android without any alterations.

This project does not aim at creating an alternative implementation of Apache HttpClient
for Android or in any form or fashion. It only provides alternative implementations of 
those components that are incompatible with Android and add several utility classes specifically
designed for Android.

## Usage

* [Apache HttpClient 4.5.x](hc4.md)

* [Apache HttpClient 5.0.x](hc5.md)