export type Nullable<T> = T | null;

export interface Employee {
  id: number;
  empNo: string;
  name?: Nullable<string>;
  deptCode?: Nullable<string>;
  locationId?: Nullable<number>;
  position?: Nullable<string>;
  // backend uses BigDecimal -> may arrive as string or number
  salary?: Nullable<number | string>;
}

export interface Department {
  id: number;
  code: string;
  name?: Nullable<string>;
}

export interface Location {
  id: number;
  code?: Nullable<string>;
  name?: Nullable<string>;
}

export interface Tier {
  id: number;
  code?: Nullable<string>;
  name?: Nullable<string>;
}