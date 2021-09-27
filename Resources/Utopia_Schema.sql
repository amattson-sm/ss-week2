-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema com.ss.utopia
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema com.ss.utopia
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `com.ss.utopia` DEFAULT CHARACTER SET utf8 ;
USE `com.ss.utopia` ;

-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_airport`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_airport` (
  `iataIdent` VARCHAR(4) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `airportId` INT NOT NULL,
  PRIMARY KEY (`iataIdent`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_users` (
  `userId` INT NOT NULL AUTO_INCREMENT,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(50) NULL DEFAULT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NULL DEFAULT NULL,
  `enabled` TINYINT(1) NOT NULL,
  `locked` TINYINT(1) NOT NULL,
  `userRole` INT NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `tbl_users_email_uindex` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 103
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_booking`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_booking` (
  `bookingId` INT NOT NULL AUTO_INCREMENT,
  `isActive` TINYINT NOT NULL,
  `stripeId` VARCHAR(45) NOT NULL,
  `bookerId` INT NOT NULL,
  PRIMARY KEY (`bookingId`),
  INDEX `fk_tbl_booking_tbl_users1_idx` (`bookerId` ASC) VISIBLE,
  CONSTRAINT `fk_tbl_booking_tbl_users1`
    FOREIGN KEY (`bookerId`)
    REFERENCES `com.ss.utopia`.`tbl_users` (`userId`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 168
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_traveler`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_traveler` (
  `travelerId` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `phone` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `dob` DATETIME NOT NULL,
  PRIMARY KEY (`travelerId`))
ENGINE = InnoDB
AUTO_INCREMENT = 91
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_bookings_has_travelers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_bookings_has_travelers` (
  `bookingId` INT NOT NULL,
  `travelerId` INT NOT NULL,
  PRIMARY KEY (`bookingId`, `travelerId`),
  UNIQUE INDEX `travelerId` (`travelerId` ASC) VISIBLE,
  INDEX `fk_tbl_bookings_has_tbl_traveler_tbl_traveler1_idx` (`travelerId` ASC) VISIBLE,
  INDEX `fk_tbl_bookings_has_tbl_traveler_tbl_bookings1_idx` (`bookingId` ASC) VISIBLE,
  CONSTRAINT `fk_tbl_bookings_has_tbl_traveler_tbl_bookings1`
    FOREIGN KEY (`bookingId`)
    REFERENCES `com.ss.utopia`.`tbl_booking` (`bookingId`),
  CONSTRAINT `fk_tbl_bookings_has_tbl_traveler_tbl_traveler1`
    FOREIGN KEY (`travelerId`)
    REFERENCES `com.ss.utopia`.`tbl_traveler` (`travelerId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_flight_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_flight_details` (
  `flightNumber` VARCHAR(4) NOT NULL,
  `departCityId` VARCHAR(4) NULL DEFAULT NULL,
  `arriveCityId` VARCHAR(4) NULL DEFAULT NULL,
  PRIMARY KEY (`flightNumber`),
  INDEX `tbl_flight_details_arriveCityId_index` (`arriveCityId` ASC) VISIBLE,
  INDEX `tbl_flight_details_departCityId_index` (`departCityId` ASC) VISIBLE,
  CONSTRAINT `tbl_flight_details_tbl_airport_iataIdent_fk`
    FOREIGN KEY (`departCityId`)
    REFERENCES `com.ss.utopia`.`tbl_airport` (`iataIdent`)
    ON UPDATE CASCADE,
  CONSTRAINT `tbl_flight_details_tbl_airport_iataIdent_fk_2`
    FOREIGN KEY (`arriveCityId`)
    REFERENCES `com.ss.utopia`.`tbl_airport` (`iataIdent`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_flight`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_flight` (
  `flightId` INT NOT NULL AUTO_INCREMENT,
  `departTime` DATETIME NOT NULL,
  `seatsAvailable` INT NOT NULL,
  `price` DECIMAL(13,2) NOT NULL,
  `arrivalTime` DATETIME NOT NULL,
  `flightNumber` VARCHAR(4) NULL DEFAULT NULL,
  PRIMARY KEY (`flightId`),
  INDEX `idx_tbl_flight_departTime` (`departTime` ASC) VISIBLE,
  INDEX `idx_tbl_flight_arrivalTime` (`arrivalTime` ASC) VISIBLE,
  INDEX `tbl_flight_tbl_flight_details_flightNumber_fk` (`flightNumber` ASC) VISIBLE,
  CONSTRAINT `tbl_flight_tbl_flight_details_flightNumber_fk`
    FOREIGN KEY (`flightNumber`)
    REFERENCES `com.ss.utopia`.`tbl_flight_details` (`flightNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 2913637
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_flight_has_bookings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_flight_has_bookings` (
  `flightId` INT NOT NULL,
  `bookingId` INT NOT NULL,
  PRIMARY KEY (`flightId`, `bookingId`),
  INDEX `fk_tbl_flights_has_tbl_bookings_tbl_bookings1_idx` (`bookingId` ASC) VISIBLE,
  INDEX `fk_tbl_flights_has_tbl_bookings_tbl_flights1_idx` (`flightId` ASC) VISIBLE,
  CONSTRAINT `fk_tbl_flights_has_tbl_bookings_tbl_bookings1`
    FOREIGN KEY (`bookingId`)
    REFERENCES `com.ss.utopia`.`tbl_booking` (`bookingId`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_tbl_flights_has_tbl_bookings_tbl_flights1`
    FOREIGN KEY (`flightId`)
    REFERENCES `com.ss.utopia`.`tbl_flight` (`flightId`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `com.ss.utopia`.`tbl_user_signup_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `com.ss.utopia`.`tbl_user_signup_token` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `confirmationToken` VARCHAR(100) NOT NULL,
  `createdDate` DATETIME NOT NULL,
  `userId` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `tbl_user_signup_token_confirmationToken_uindex` (`confirmationToken` ASC) VISIBLE,
  INDEX `tbl_user_signup_token_tbl_users_userId_fk` (`userId` ASC) VISIBLE,
  CONSTRAINT `tbl_user_signup_token_tbl_users_userId_fk`
    FOREIGN KEY (`userId`)
    REFERENCES `com.ss.utopia`.`tbl_users` (`userId`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 67
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
