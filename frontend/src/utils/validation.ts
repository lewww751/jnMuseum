/**
 * Validates Chinese ID card number (18 digits) with GB11643 checksum
 * @param idCard ID card number to validate
 * @returns true if valid, false otherwise
 */
export const validateIdCard = (idCard: string): boolean => {
  // Remove spaces and convert to uppercase
  const cleanIdCard = idCard.replace(/\s/g, '').toUpperCase()

  // Check length
  if (cleanIdCard.length !== 18) {
    return false
  }

  // Check format: first 17 digits, last digit can be X or digit
  const regex = /^\d{17}[\dX]$/
  if (!regex.test(cleanIdCard)) {
    return false
  }

  // Calculate checksum
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  const checksumChars = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(cleanIdCard[i]) * weights[i]
  }

  const checksum = checksumChars[sum % 11]
  return cleanIdCard[17] === checksum
}

/**
 * Masks ID card number for display (show first 4 and last 4 digits)
 * @param idCard ID card number to mask
 * @returns Masked ID card number
 */
export const maskIdCard = (idCard: string): string => {
  if (idCard.length < 8) return idCard
  return idCard.substring(0, 4) + '********' + idCard.substring(idCard.length - 4)
}

/**
 * Validates Chinese mobile phone number
 * @param phone Phone number to validate
 * @returns true if valid, false otherwise
 */
export const validatePhone = (phone: string): boolean => {
  const regex = /^1[3-9]\d{9}$/
  return regex.test(phone)
}