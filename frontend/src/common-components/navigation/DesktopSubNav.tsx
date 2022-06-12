import { Box, Stack, Flex, Icon, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaChevronRight } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { INavItem } from '../../util/configs/nav.config'

type Props = {
  navItem: INavItem
}

export const DesktopSubNav = ({ navItem: { label, path, icon } }: Props) => (
  <Link to={path || '#'}>
    <Box role="group" display="block" p={2} rounded="md">
      <Stack direction="row" align="center">
        <Box>
          <Text transition="all .3s ease" _groupHover={{ color: useColorModeValue('brand.500', 'brand.600') }} fontWeight={500}>
            {label}
          </Text>
        </Box>
        <Flex
          transition="all .3s ease"
          transform="translateX(-10px)"
          opacity={0}
          _groupHover={{ opacity: '100%', transform: 'translateX(0)' }}
          justify="flex-end"
          align="center"
          flex={1}
        >
          <Icon color={useColorModeValue('brand.500', 'brand.600')} w={5} h={5} as={FaChevronRight} />
        </Flex>
      </Stack>
    </Box>
  </Link>
)
