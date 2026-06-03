// src/CarbonCredit.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

contract CarbonCredit is ERC1155, Ownable {
    uint256 public nextCreditId = 1;

    struct Credit {
        uint256 farmId;
        uint256 carbonKg;     // Named carbonKg for clarity as per system spec
        uint256 timestamp;
        string ndviDataHash;  // SHA256 of satellite data
        bool sold;
    }

    mapping(uint256 => Credit) public credits;
    // Track the original farmer who harvested the credit
    mapping(uint256 => address) public creditFarmer;

    event CreditMinted(uint256 indexed creditId, uint256 indexed farmId, uint256 carbonKg);
    event CreditSold(uint256 indexed creditId, address indexed buyer, address indexed farmer, uint256 price);

    constructor() ERC1155("") Ownable(msg.sender) {}

    // Called by Shamba Guard backend when carbon is verified via AI models
    function mintCredit(
        address farmer,
        uint256 farmId,
        uint256 carbonKg,
        string memory ndviHash
    ) external onlyOwner returns (uint256) {
        uint256 id = nextCreditId++;
        
        credits[id] = Credit({
            farmId: farmId,
            carbonKg: carbonKg,
            timestamp: block.timestamp,
            ndviDataHash: ndviHash,
            sold: false
        });
        
        creditFarmer[id] = farmer;
        
        // Mint the 1-of-1 semi-fungible credit token directly to the farmer
        _mint(farmer, id, 1, "");
        
        emit CreditMinted(id, farmId, carbonKg);
        return id;
    } https://faucet.polygon.technology

    // Called by a buyer to purchase a credit directly from the marketplace functionality
    function purchaseCredit(uint256 creditId) external payable {
        Credit storage credit = credits[creditId];
        require(!credit.sold, "Credit already sold");
        require(msg.value > 0, "Payment required");
        
        address farmer = creditFarmer[creditId];
        require(farmer != address(0), "Invalid credit ID");

        // Verify the farmer still owns the credit token before routing payment
        require(balanceOf(farmer, creditId) == 1, "Farmer no longer holds token");

        credit.sold = true;
        
        // Calculate the transparent 90% payout splitting out middlemen manipulations
        uint256 farmerShare = (msg.value * 90) / 100;
        uint256 platformShare = msg.value - farmerShare;

        // Execute secure token transfer from farmer to buyer
        // Uses the internal _safeTransferFrom to bypass third-party approval requirements
        _safeTransferFrom(farmer, msg.sender, creditId, 1, "");

        // Route payments
        payable(farmer).transfer(farmerShare);
        payable(owner()).transfer(platformShare); // Remaining 10% platform fee to contract owner

        emit CreditSold(creditId, msg.sender, farmer, msg.value);
    }
}