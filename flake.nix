{
  description = "A very basic flake";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
annulusLibs = with pkgs; [
  jdk21
  libGL
  flite
  xorg.libX11
  xorg.libXcursor
  xorg.libXrandr
  xorg.libXinerama
  xorg.libXi
  xorg.libXext
];
   in
    {
      packages.${system} = {
        hello = pkgs.hello;
        default = self.packages.${system}.hello;
      };

      devShells.${system}.default = pkgs.mkShell {
        buildInputs = annulusLibs;

        shellHook = ''
          export LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath annulusLibs}:$LD_LIBRARY_PATH
        '';
      };
    };
}