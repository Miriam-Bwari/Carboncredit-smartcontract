// script/DeployCarbonCredit.s.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "forge-std/Script.sol";
import "../src/CarbonCredit.sol";

contract DeployCarbonCredit is Script {
    function run() external {
        // Retrieve the private key from your local system environment variables
        uint256 deployerPrivateKey = vm.envUint("DEPLOYER_PRIVATE_KEY");

        // Everything inside the broadcast block will create a real broadcasted transaction
        vm.startBroadcast(deployerPrivateKey);

        CarbonCredit carbonCredit = new CarbonCredit();
        
        console.log("CarbonCredit deployed to address:", address(carbonCredit));

        vm.stopBroadcast();
    }
}