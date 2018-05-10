#!/bin/env ruby

puts (`lein clean` or abort("Could not clean up. No leiningen?"))
puts (`lein pom` or abort("Could not make pom file"))
puts (`lein compile` or abort("Could not compile!"))
puts (`mvn package -DskipTests` or abort("Could not make jar package"))

JARFILE = Dir.glob("target/stencil-*.jar").find { |fn| not fn.include? "-standalone" }

abort('Missing jar file, build it first!') unless JARFILE

GROUP = "io/github/erdos"
ARTIFACT = "stencil"
VERSION = /stencil-(.*).jar/.match(JARFILE)[1]

puts "Csoport: #{GROUP}"
puts "Artifakt: #{ARTIFACT}"
puts "Verzio: #{VERSION}"

REMOTE_DIRECTORY="/home/ubuntu/maven/repository/#{GROUP}/#{ARTIFACT}/#{VERSION}"
puts (`ssh dbx.services mkdir -p #{REMOTE_DIRECTORY}` or abort("Could not create dir"))

REMOTE_FILE="#{REMOTE_DIRECTORY}/stencil-#{VERSION}.jar"
puts (`scp #{JARFILE} dbx.services:#{REMOTE_FILE}` or abort("Could not copy!"))

puts "Good!"