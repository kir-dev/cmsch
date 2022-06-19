import { Flex, HStack, Tag, Text, Image, useColorModeValue, WrapItem } from '@chakra-ui/react'
import { customTheme } from '../../../util/configs/theme.config'
import { Dev } from './developers'

const DeveloperCard = ({ name, img, tags }: Dev) => {
  return (
    <WrapItem border="2px" borderColor={useColorModeValue('gray.200', 'gray.700')} borderRadius="md" key={name}>
      <Flex direction="column" align="center" w="20rem" h="20rem">
        <Text fontSize="2xl">{name}</Text>
        <Image src={img} h="15rem" fallbackSrc="/img/big_pear_logo.png" />
        <HStack spacing={2} my={2}>
          {tags.map((tag) => (
            <Tag size={'md'} variant="solid" fontWeight="bold" color="white" bgColor={customTheme.colors.kirDev} key={tag}>
              {tag}
            </Tag>
          ))}
        </HStack>
      </Flex>
    </WrapItem>
  )
}

export default DeveloperCard
