import { useMemo } from 'react';
import {hexToRgb, rgbToHex} from "./color.utils.ts";


function muteColor(hex: string, desat: number, dim: number): string {
  const [ r, g, b ] = hexToRgb(hex);

  const gray = 0.3 * r + 0.59 * g + 0.11 * b;

  const rDesat = r * (1 - desat) + gray * desat;
  const gDesat = g * (1 - desat) + gray * desat;
  const bDesat = b * (1 - desat) + gray * desat;

  const rDim = rDesat * (1 - dim);
  const gDim = gDesat * (1 - dim);
  const bDim = bDesat * (1 - dim);

  return rgbToHex(rDim, gDim, bDim);
}


export function useMutedColor(hex: string, desat: number = 0.5, dim = 0.2): string {
  return useMemo(() => muteColor(hex, desat, dim), [hex, desat]);
}
