import { Box, Collapse, Stack, useDisclosure } from '@chakra-ui/react'
import { Menu } from '../../../api/contexts/config/types'
import { HeaderHasUrl } from './HeaderHasUrl'
import { HeaderNoUrl } from './HeaderNoUrl'
import { NavItemNoChildren } from './NavItemNoChildren'

type Props = {
  menu: Menu
  onButtonClickSideEffect: () => void
}

export const NavItemWithChildren = ({ menu, onButtonClickSideEffect }: Props) => {
  const { isOpen, onToggle } = useDisclosure()

  return (
    <Box w="full">
      {menu.url === '' ? (
        <HeaderNoUrl menu={menu} isOpen={isOpen} onToggle={onToggle} />
      ) : (
        <HeaderHasUrl menu={menu} isOpen={isOpen} onToggle={onToggle} onButtonClickSideEffect={onButtonClickSideEffect} />
      )}
      <Collapse in={isOpen} animateOpacity>
        <Stack pl={6} pt={2}>
          {menu.children.map((menu) =>
            menu.children && menu.children.length > 0 ? (
              <NavItemWithChildren key={menu.name} menu={menu} onButtonClickSideEffect={onButtonClickSideEffect} />
            ) : (
              <NavItemNoChildren key={menu.name} menu={menu} onButtonClickSideEffect={onButtonClickSideEffect} />
            )
          )}
        </Stack>
      </Collapse>
    </Box>
  )
}
