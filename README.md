1. Ensure that this "site" project folder is in a folder which also contains a project 
 folder for a buildable branch of Kotlin for Forge (Ex. a folder called "projects" which 
contains "website" folder for the "site" branch and "KotlinForForge" for the "4.x" branch)

2. Run update_maven.jl with Julia
```shell
julia update_maven.jl <kff mod branch folder>
```

credits: [thedarkcolour](https://github.com/thedarkcolour/KotlinForForge/tree/site)